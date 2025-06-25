package kr.toxicity.model

import com.vdurmont.semver4j.Semver
import kr.toxicity.model.api.BetterModel
import kr.toxicity.model.api.BetterModelLogger
import kr.toxicity.model.api.BetterModelPlugin
import kr.toxicity.model.api.BetterModelPlugin.ReloadResult
import kr.toxicity.model.api.BetterModelPlugin.ReloadResult.*
import kr.toxicity.model.api.manager.*
import kr.toxicity.model.api.nms.NMS
import kr.toxicity.model.api.scheduler.ModelScheduler
import kr.toxicity.model.api.tracker.EntityTrackerRegistry
import kr.toxicity.model.api.util.HttpUtil
import kr.toxicity.model.api.version.MinecraftVersion
import kr.toxicity.model.api.version.MinecraftVersion.*
import kr.toxicity.model.manager.*
import kr.toxicity.model.scheduler.PaperScheduler
import kr.toxicity.model.scheduler.StandardScheduler
import kr.toxicity.model.util.*
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer
import java.util.jar.JarFile
import java.util.jar.Manifest
import java.util.zip.ZipEntry

@Suppress("UNUSED")
class BetterModelPluginImpl : JavaPlugin(), BetterModelPlugin {
    private val version = MinecraftVersion(Bukkit.getBukkitVersion().substringBefore('-'))
    private lateinit var nms: NMS

    private val onReload = AtomicBoolean()

    private val managers by lazy {
        listOf(
            CompatibilityManagerImpl,
            ConfigManagerImpl,
            SkinManagerImpl,
            ModelManagerImpl,
            PlayerManagerImpl,
            EntityManagerImpl,
            ScriptManagerImpl,
            CommandManagerImpl
        )
    }

    private val scheduler = if (BetterModel.IS_FOLIA) PaperScheduler() else StandardScheduler()
    private val log = object : BetterModelLogger {
        private val internalLogger = logger
        override fun info(vararg message: String) {
            synchronized(internalLogger) {
                for (s in message) {
                    internalLogger.info(s)
                }
            }
        }
        override fun warn(vararg message: String) {
            synchronized(internalLogger) {
                for (s in message) {
                    internalLogger.warning(s)
                }
            }
        }
    }
    @Suppress("DEPRECATION") //To support Spigot :(
    private val semver = Semver(description.version, Semver.SemverType.LOOSE)
    private val audiences by lazy {
        BukkitAudiences.create(this)
    }
    private val snapshot by lazy {
        runCatching {
            JarFile(file).use {
                it.getInputStream(ZipEntry("META-INF/MANIFEST.MF")).use { stream ->
                    Manifest(stream).mainAttributes.getValue("Dev-Build").toInt()
                }
            }
        }.getOrElse {
            it.handleException("Unable to parse manifest.")
            -1
        }
    }

    private var reloadStartTask: () -> Unit = {}
    private var reloadEndTask: (ReloadResult) -> Unit = {}

    override fun onLoad() {
        BetterModel.register(this)
    }

    override fun onEnable() {
        audiences()
        nms = when (version) { //TODO: Add 1.21.7 NMS
            V1_21_6 -> kr.toxicity.model.nms.v1_21_R5.NMSImpl()
            V1_21_5 -> kr.toxicity.model.nms.v1_21_R4.NMSImpl()
            V1_21_4 -> kr.toxicity.model.nms.v1_21_R3.NMSImpl()
            V1_21_2, V1_21_3 -> kr.toxicity.model.nms.v1_21_R2.NMSImpl()
            V1_21, V1_21_1 -> kr.toxicity.model.nms.v1_21_R1.NMSImpl()
            V1_20_5, V1_20_6 -> kr.toxicity.model.nms.v1_20_R4.NMSImpl()
            V1_20_3, V1_20_4 -> kr.toxicity.model.nms.v1_20_R3.NMSImpl()
            else -> {
                warn(
                    "Unsupported version: $version",
                    "Plugin will be automatically disabled."
                )
                Bukkit.getPluginManager().disablePlugin(this)
                return
            }
        }
        managers.forEach(GlobalManagerImpl::start)
        val latestVersion = HttpUtil.versionList()
        val versionNoticeList = arrayListOf<Component>()
        latestVersion.release?.let {
            if (semver < it.versionNumber()) versionNoticeList += Component.text("New BetterModel release found: ").append(it.toURLComponent())
        }
        latestVersion.snapshot?.let {
            if (semver < it.versionNumber()) versionNoticeList += Component.text("New BetterModel snapshot found: ").append(it.toURLComponent())
        }
        if (versionNoticeList.isNotEmpty()) {
            registerListener(object : Listener {
                @EventHandler
                fun PlayerJoinEvent.join() {
                    if (!player.isOp || !ConfigManagerImpl.versionCheck()) return
                    versionNoticeList.forEach {
                        audiences.player(player).sendMessage(it)
                    }
                }
            })
        }
        if (isSnapshot) warn(
            "This build is dev version: be careful to use it!",
            "Build number: $snapshot"
        )
        when (val result = reload(ReloadInfo(DATA_FOLDER.exists()))) {
            is Failure -> result.throwable.handleException("Unable to load plugin properly.")
            is OnReload -> throw RuntimeException("Plugin load failed.")
            is Success -> info(
                "Plugin is loaded. (${result.time.withComma()} ms)",
                "Minecraft version: $version, NMS version: ${nms.version()}",
                "Platform: ${when {
                    BetterModel.IS_FOLIA -> "Folia"
                    BetterModel.IS_PURPUR -> "Purpur"
                    BetterModel.IS_PAPER -> "Paper"
                    else -> "Bukkit"
                }}"
            )
        }
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach { EntityTrackerRegistry.registry(it.uniqueId)?.close() }
        audiences.close()
        managers.forEach(GlobalManagerImpl::end)
    }

    override fun reload(info: ReloadInfo): ReloadResult {
        if (!onReload.compareAndSet(false, true)) return ON_RELOAD
        reloadStartTask()
        val result = runCatching {
            val time = System.currentTimeMillis()
            managers.forEach {
                it.reload(info)
            }
            Success(System.currentTimeMillis() - time)
        }.getOrElse {
            Failure(it)
        }
        reloadEndTask(result)
        onReload.set(false)
        return result
    }

    fun loadAssets(prefix: String, consumer: (String, InputStream) -> Unit) {
        JarFile(file).use {
            it.entries().toList().forEachAsync { entry ->
                if (!entry.name.startsWith(prefix)) return@forEachAsync
                if (entry.name.length <= prefix.length + 1) return@forEachAsync
                val name = entry.name.substring(prefix.length + 1)
                if (!entry.isDirectory) it.getInputStream(entry).buffered().use { stream ->
                    consumer(name, stream)
                }
            }
        }
    }

    override fun logger(): BetterModelLogger = log
    override fun scheduler(): ModelScheduler = scheduler
    override fun modelManager(): ModelManager = ModelManagerImpl
    override fun playerManager(): PlayerManager = PlayerManagerImpl
    override fun entityManager(): EntityManager = EntityManagerImpl
    override fun commandManager(): CommandManager = CommandManagerImpl
    override fun compatibilityManager(): CompatibilityManager = CompatibilityManagerImpl
    override fun configManager(): ConfigManager = ConfigManagerImpl
    override fun scriptManager(): ScriptManager = ScriptManagerImpl
    override fun skinManager(): SkinManager = SkinManagerImpl

    override fun version(): MinecraftVersion = version
    override fun semver(): Semver = semver
    override fun nms(): NMS = nms
    override fun audiences(): BukkitAudiences = audiences
    override fun isSnapshot(): Boolean = snapshot > 0

    @Synchronized
    override fun addReloadStartHandler(runnable: Runnable) {
        val previous = reloadStartTask
        reloadStartTask = {
            previous()
            runnable.run()
        }
    }

    @Synchronized
    override fun addReloadEndHandler(consumer: Consumer<ReloadResult>) {
        val previous = reloadEndTask
        reloadEndTask = {
            previous(it)
            consumer.accept(it)
        }
    }
}