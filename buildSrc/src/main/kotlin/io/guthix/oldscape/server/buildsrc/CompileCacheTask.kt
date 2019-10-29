package io.guthix.oldscape.server.buildsrc

import io.guthix.cache.js5.Js5ArchiveSettings
import io.guthix.cache.js5.Js5ArchiveValidator
import io.guthix.cache.js5.Js5CacheValidator
import io.guthix.cache.js5.container.Js5Container
import io.guthix.cache.js5.container.disk.Js5DiskStore
import io.guthix.cache.js5.util.crc
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.ceil

open class CompileCacheTask : DefaultTask() {
    val cacheDir = Path.of("${project.projectDir}\\src\\main\\resources\\cache")
    val buildDir = Path.of("${project.buildDir.path}\\resources\\main\\cache")

    @TaskAction
    fun execute() {
        if(!Files.exists(buildDir)) Files.createDirectories(buildDir)
        val ds = Js5DiskStore.open(cacheDir)
        val masterDir = buildDir.resolve(Js5DiskStore.MASTER_INDEX.toString())
        if(!Files.isDirectory(masterDir)) Files.createDirectory(masterDir)
        var rebuildValidator = false
        val archiveSettingsData = mutableMapOf<Int, ByteBuf>()
        val archiveSettings = mutableMapOf<Int, Js5ArchiveSettings>()
        for(archiveId in 0 until ds.archiveCount) { // update archive settings and data
            val archiveIdx = ds.openArchiveIdxFile(archiveId)
            val archiveDir = buildDir.resolve(archiveId.toString())
            if(!Files.isDirectory(archiveDir)) Files.createDirectory(archiveDir)
            val readSettingsData = ds.read(ds.masterIdxFile, archiveId)
            val readSettings = Js5ArchiveSettings.decode(Js5Container.decode(readSettingsData))
            readSettingsData.readerIndex(0)
            val readSettingsPacket = createPacket(Js5DiskStore.MASTER_INDEX, archiveId, readSettingsData)
            val settingsFile = masterDir.resolve(archiveId.toString())
            if(Files.exists(settingsFile)) {
                val storedSettingsPacket = Unpooled.wrappedBuffer(
                    Files.readAllBytes(settingsFile)
                )
                archiveSettingsData[archiveId] = readSettingsData
                archiveSettings[archiveId]= readSettings
                if(readSettingsPacket != storedSettingsPacket) {
                    rebuildValidator = true
                    val storedSettings = Js5ArchiveSettings.decode(
                        Js5Container.decode(unpackPacket(storedSettingsPacket))
                    )
                    for((groupId, rGroupSettings) in readSettings.groupSettings) {
                        val sGroupSettings = storedSettings.groupSettings[groupId]
                        val groupFile = archiveDir.resolve(groupId.toString())
                        if(sGroupSettings == null) { // create new file
                            val packet = createPacket(archiveId, groupId, ds.read(archiveIdx, groupId))
                            Files.createFile(groupFile)
                            Files.write(groupFile, packet.array())
                            logger.info("Creating archive $archiveId group $groupId")
                        } else { // check if file is still up to date
                            if(rGroupSettings.crc != sGroupSettings.crc) {
                                val packet = createPacket(archiveId, groupId, ds.read(archiveIdx, groupId))
                                Files.deleteIfExists(groupFile)
                                Files.createFile(groupFile)
                                Files.write(groupFile, packet.array())
                                logger.info("Updating archive $archiveId group $groupId")
                            }
                        }
                    }
                    Files.deleteIfExists(settingsFile)
                    Files.createFile(settingsFile)
                    Files.write(settingsFile, readSettingsPacket.array())
                    logger.info("Creating settings for $archiveId")
                }
            } else {
                rebuildValidator = true
                Files.createFile(settingsFile)
                Files.write(settingsFile, readSettingsPacket.array())
                for((groupId, _) in readSettings.groupSettings) {
                    val groupFile = archiveDir.resolve(groupId.toString())
                    val packet = createPacket(archiveId, groupId, ds.read(archiveIdx, groupId))
                    Files.createFile(groupFile)
                    Files.write(groupFile, packet.array())
                    logger.info("Creating archive $archiveId group $groupId")
                }
            }
        }
        if(rebuildValidator) { // update validator if needed
            val archiveValidators = mutableListOf<Js5ArchiveValidator>()
            for((archiveId, settings) in archiveSettings) {
                val data = archiveSettingsData[archiveId] ?: throw IllegalStateException(
                    "Archive data does not exist."
                )
                val validator = Js5ArchiveValidator(data.crc(), settings.version, null, null, null)
                archiveValidators.add(validator)
            }
            val validatorData = Js5CacheValidator(archiveValidators.toTypedArray()).encode()
            val validatorPacket = createPacket(
                Js5DiskStore.MASTER_INDEX, Js5DiskStore.MASTER_INDEX, Js5Container(data = validatorData).encode()
            )
            val validatorFile = masterDir.resolve(Js5DiskStore.MASTER_INDEX.toString())
            Files.deleteIfExists(validatorFile)
            Files.createFile(validatorFile)
            Files.write(validatorFile, validatorPacket.array())
        }
    }

    private fun createPacket(indexFileId: Int, containerId: Int, data: ByteBuf): ByteBuf {
        val dataSize = data.writerIndex()
        val packetBuf = Unpooled.buffer(HEADER_SIZE + dataSize + ceil(
            (dataSize - BYTES_AFTER_HEADER) / BYTES_AFTER_BLOCK.toDouble()
        ).toInt())
        packetBuf.writeByte(indexFileId)
        packetBuf.writeShort(containerId)
        val firstSectorSize = if(dataSize > BYTES_AFTER_HEADER) BYTES_AFTER_HEADER else dataSize
        packetBuf.writeBytes(data, firstSectorSize)
        while(data.isReadable) {
            val dataToRead = data.readableBytes()
            val sectorSize = if(dataToRead > BYTES_AFTER_BLOCK) BYTES_AFTER_BLOCK else dataToRead
            packetBuf.writeByte(BLOCK_HEADER)
            packetBuf.writeBytes(data, sectorSize)
        }
        return packetBuf
    }

    private fun unpackPacket(data: ByteBuf) : ByteBuf {
        data.readUnsignedByte() // index file id
        data.readUnsignedShort() // container id
        val dataLeft = data.writerIndex() - data.readerIndex()
        val resultBuffer = Unpooled.buffer(dataLeft - ceil(
            (dataLeft - BYTES_AFTER_HEADER) / BYTES_AFTER_BLOCK.toDouble()
        ).toInt())
        data.readBytes(resultBuffer, BYTES_AFTER_HEADER)
        while(data.isReadable) {
            check(data.readUnsignedByte().toInt() == 0xFF) { "First block byte should be equal to 0xFF" }
            data.readBytes(resultBuffer, BYTES_AFTER_BLOCK)
        }
        return data
    }

    companion object {
        private const val SECTOR_SIZE = 512
        private const val HEADER_SIZE = Byte.SIZE_BYTES + Short.SIZE_BYTES
        private const val BYTES_AFTER_HEADER = SECTOR_SIZE - HEADER_SIZE
        private const val BYTES_AFTER_BLOCK = SECTOR_SIZE - 1
        private const val BLOCK_HEADER = 255
    }

}