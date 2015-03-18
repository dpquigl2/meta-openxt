# XenClient Synchronizer client VM image

include openxt-image-common.inc
IMAGE_FEATURES += "package-management"

COMPATIBLE_MACHINE = "(openxt-syncvm)"

IMAGE_FSTYPES = "xc.ext3.vhd.gz"

ANGSTROM_EXTRA_INSTALL += ""

export IMAGE_BASENAME = "openxt-syncvm-image"

FRIENDLY_NAME = "syncvm"

DEPENDS = "packagegroup-base"

# ifplugd removed as busybox is now >= 1.15
IMAGE_INSTALL = "\
    ${ROOTFS_PKGMANAGE} \
    modules \
    packagegroup-core-boot \
    packagegroup-base \
    packagegroup-openxt-common \
    bootage \
    kernel-modules \
    v4v-module \
    libv4v \
    libv4v-bin \
    rsyslog \
    openssh \
    blktap \
    ifplugd \
    wget \
    sync-client \
    openxt-syncvm-tweaks \
    ${ANGSTROM_EXTRA_INSTALL}"

#IMAGE_PREPROCESS_COMMAND = "create_etc_timestamp"

after_commands() {
    echo 'ca:12345:ctrlaltdel:/sbin/shutdown -t1 -a -r now' >> ${IMAGE_ROOTFS}/etc/inittab;
    sed -i 's|root:x:0:0:root:/home/root:/bin/sh|root:x:0:0:root:/root:/bin/bash|' ${IMAGE_ROOTFS}/etc/passwd;
    echo '1.0.0.0 dom0' >> ${IMAGE_ROOTFS}/etc/hosts;
    rm -f ${IMAGE_ROOTFS}/etc/resolv.conf;
    ln -s /var/volatile/etc/resolv.conf ${IMAGE_ROOTFS}/etc/resolv.conf;
    rm -f ${IMAGE_ROOTFS}/etc/network/interfaces;
    ln -s /var/volatile/etc/network/interfaces ${IMAGE_ROOTFS}/etc/network/interfaces;
}

remove_initscripts() {
    # Remove unneeded initscripts
    if [ -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/urandom ]; then
        rm -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/urandom
        update-rc.d -r ${IMAGE_ROOTFS} urandom remove
    fi
}

support_vmlinuz() {
	# Make a vmlinuz link for items that explicitly reference it
	ln -sf bzImage ${IMAGE_ROOTFS}/boot/vmlinuz
}

# Symlink /root to /home/root until nothing references /root anymore, e.g. SELinux file_contexts
link_root_dir() {
    ln -sf /home/root ${IMAGE_ROOTFS}/root
}

ROOTFS_POSTPROCESS_COMMAND += " after_commands; remove_initscripts; support_vmlinuz; link_root_dir;"

addtask ship before do_build after do_rootfs

inherit image openxt
#inherit validate-package-versions
inherit openxt-image-src-info
inherit openxt-image-src-package
inherit openxt-licences
require openxt-version.inc

LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://${TOPDIR}/COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe      \
                    file://${TOPDIR}/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
