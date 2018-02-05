package org.zstack.test.integration.kvm.vm

import org.zstack.kvm.KVMGlobalConfig
import org.zstack.sdk.*
import org.zstack.storage.primary.PrimaryStorageGlobalConfig
import org.zstack.test.integration.kvm.KvmTest
import org.zstack.testlib.EnvSpec
import org.zstack.testlib.SubCase
import org.zstack.testlib.Test
import org.zstack.utils.data.SizeUnit
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created by lining on 2018/1/14.
 */
class BatchCreateVmFailOnLocalStorageCase extends SubCase{
    EnvSpec env

    @Override
    void clean() {
        env.delete()
    }

    @Override
    void setup() {
        useSpring(KvmTest.springSpec)
    }

    @Override
    void environment() {
        env = env {
            instanceOffering {
                name = "instanceOffering"
                memory = SizeUnit.GIGABYTE.toByte(1)
                cpu = 1
            }

            diskOffering {
                name = "diskOffering"
                    diskSize = SizeUnit.GIGABYTE.toByte(1)
            }

            sftpBackupStorage {
                name = "sftp"
                url = "/sftp"
                username = "root"
                password = "password"
                hostname = "localhost"

                image {
                    name = "iso"
                    url  = "http://zstack.org/download/test.iso"
                }
            }

            zone {
                name = "zone"
                description = "test"

                cluster {
                    name = "cluster"
                    hypervisorType = "KVM"

                    kvm {
                        name = "kvm"
                        managementIp = "localhost"
                        username = "root"
                        password = "password"
                        totalCpu = 1
                        totalMem = SizeUnit.GIGABYTE.toByte(12)
                    }

                    kvm {
                        name = "kvm1"
                        managementIp = "127.0.0.2"
                        username = "root"
                        password = "password"
                        totalCpu = 1
                        totalMem = SizeUnit.GIGABYTE.toByte(12)
                    }

                    attachPrimaryStorage("local")
                    attachL2Network("l2")
                }

                localPrimaryStorage {
                    name = "local"
                    url = "/local_ps"
                    availableCapacity = SizeUnit.GIGABYTE.toByte(5)
                    totalCapacity = SizeUnit.GIGABYTE.toByte(5)
                }

                l2NoVlanNetwork {
                    name = "l2"
                    physicalInterface = "eth0"

                    l3Network {
                        name = "l3"

                        ip {
                            startIp = "192.168.100.10"
                            endIp = "192.168.100.100"
                            netmask = "255.255.255.0"
                            gateway = "192.168.100.1"
                        }
                    }
                }

                attachBackupStorage("sftp")
            }
        }
    }

    @Override
    void test() {
        env.create {
            testBatchCreateVm()
        }
    }

    void testBatchCreateVm() {
        KVMGlobalConfig.RESERVED_MEMORY_CAPACITY.updateValue("0G")
        KVMGlobalConfig.RESERVED_CPU_CAPACITY.updateValue(0)
        PrimaryStorageGlobalConfig.RESERVED_CAPACITY.updateValue(0)

        PrimaryStorageInventory ps = env.inventoryByName("local")
        InstanceOfferingInventory instanceOffering = env.inventoryByName("instanceOffering")
        L3NetworkInventory l3 = env.inventoryByName("l3")
        ImageInventory image = env.inventoryByName("iso")
        DiskOfferingInventory diskOffering = env.inventoryByName("diskOffering")

        int errorNum = 0

        final CountDownLatch latch = new CountDownLatch(10)
        for (int i = 0; i < 10; i++) {
            CreateVmInstanceAction action = new CreateVmInstanceAction(
                    name : "test-" + i,
                    instanceOfferingUuid : instanceOffering.uuid,
                    l3NetworkUuids : [l3.uuid],
                    imageUuid : image.uuid,
                    rootDiskOfferingUuid : diskOffering.uuid,
                    dataDiskOfferingUuids: [diskOffering.uuid],
                    sessionId: Test.currentEnvSpec.session.uuid
            )

            action.call(new Completion<CreateVmInstanceAction.Result>() {
                @Override
                void complete(CreateVmInstanceAction.Result ret) {
                    latch.countDown()

                    if(ret.error != null){
                        errorNum ++
                    }
                }
            })
        }

        latch.await(15, TimeUnit.SECONDS)
        assert errorNum >= 0

        ps = queryPrimaryStorage {
            conditions=["uuid=${ps.uuid}".toString()]
        }[0]
        assert SizeUnit.GIGABYTE.toByte(errorNum) == ps.availableCapacity

    }
}
