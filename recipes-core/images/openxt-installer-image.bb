# Part 1 of the XenClient host installer.
#
# This is responsible for retrieving the XenClient repository and extracting
# and running part 2 of the host installer, which contains the logic to install
# or upgrade a specific version of XenClient.

LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://${TOPDIR}/COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe      \
                    file://${TOPDIR}/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

include openxt-image-common.inc

COMPATIBLE_MACHINE = "(openxt-dom0)"
IMAGE_INITSCRIPTS = "initscripts"

PR = "r15"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
            file://network.ans \
            file://network_upgrade.ans \
            file://network_manual.ans \
            file://network_download_win.ans \
            file://network_manual_download_win.ans \
            file://pxelinux.cfg \
            file://isolinux.cfg \
            file://bootmsg.txt \
"

ANGSTROM_EXTRA_INSTALL += ""

export IMAGE_BASENAME = "openxt-installer-image"

DEPENDS = "packagegroup-base packagegroup-openxt-installer"

IMAGE_INSTALL = "\
    ${ROOTFS_PKGMANAGE} \
    ${IMAGE_INITSCRIPTS} \
    modules-installer \
    linux-firmware \
    packagegroup-core-boot \
    packagegroup-base \
    packagegroup-openxt-common \
    packagegroup-openxt-installer \
    ${ANGSTROM_EXTRA_INSTALL}"

IMAGE_FSTYPES = "cpio.gz"

# IMAGE_PREPROCESS_COMMAND = "create_etc_timestamp"

inherit image openxt
inherit openxt-image-src-info
inherit openxt-image-src-package
inherit openxt-licences
require openxt-version.inc

post_rootfs_commands() {
	# Create /init symlink
	ln -s sbin/init ${IMAGE_ROOTFS}/init;

	# Update /etc/inittab
	sed -i '/^1:/d' ${IMAGE_ROOTFS}/etc/inittab;
	{
		echo '1:2345:once:/install/part1/autostart-main < /dev/tty1 > /dev/tty1';
		echo '2:2345:respawn:/usr/bin/tail -F /var/log/installer > /dev/tty2';
		echo '3:2345:respawn:/sbin/getty 38400 tty3';
		echo '4:2345:respawn:/usr/bin/tail -F /var/log/messages > /dev/tty4';
		echo '5:2345:respawn:/sbin/getty 38400 tty5';
		echo '6:2345:respawn:/sbin/getty 38400 tty6';
		echo '7:2345:respawn:/install/part1/autostart-status < /dev/tty7 > /dev/tty7';
		echo 'ca::ctrlaltdel:/sbin/reboot';
	} >> ${IMAGE_ROOTFS}/etc/inittab;
	
	# Update /etc/fstab
	sed -i '/^\/dev\/mapper\/openxt/d' ${IMAGE_ROOTFS}/etc/fstab;

	# Update /etc/network/interfaces
	{
		echo 'auto lo';
		echo 'iface lo inet loopback';
	} > ${IMAGE_ROOTFS}/etc/network/interfaces;

	# Password files are expected in /config
	mkdir -p ${IMAGE_ROOTFS}/config/etc;
	mv ${IMAGE_ROOTFS}/etc/shadow ${IMAGE_ROOTFS}/config/etc/shadow;
	mv ${IMAGE_ROOTFS}/etc/passwd ${IMAGE_ROOTFS}/config/etc/passwd;
	ln -s /config/etc/shadow ${IMAGE_ROOTFS}/etc/shadow; 
	ln -s /config/etc/passwd ${IMAGE_ROOTFS}/etc/passwd;

	# Use bash as login shell
	sed -i 's|root:x:0:0:root:/home/root:/bin/sh|root:x:0:0:root:/root:/bin/bash|' ${IMAGE_ROOTFS}/config/etc/passwd;

	# Don't start blktapctrl daemon
	rm -f ${IMAGE_ROOTFS}/etc/init.d/blktap;
	rm -f ${IMAGE_ROOTFS}/etc/rc*.d/*blktap;

	# Create file to identify this as the host installer filesystem
	touch ${IMAGE_ROOTFS}/etc/openxt-host-installer;
}

ROOTFS_POSTPROCESS_COMMAND += " post_rootfs_commands; "

do_post_rootfs_items() {
    install -d ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}/netboot
    install -m 0644 ${IMAGE_ROOTFS}/${datadir}/syslinux/mboot.c32 ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}/netboot/
    for i in ${WORKDIR}/*.ans ; do
        install -m 0644 ${i} ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}/netboot/
    done
    install -m 0644 ${WORKDIR}/pxelinux.cfg ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}/netboot/
    install -d ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}/iso
    install -m 0644 ${IMAGE_ROOTFS}/${datadir}/syslinux/mboot.c32 ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}/iso/
    install -m 0644 ${IMAGE_ROOTFS}/${datadir}/syslinux/pxelinux.0 ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}/iso/
    install -m 0644 ${WORKDIR}/bootmsg.txt ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}/iso/
    install -m 0644 ${IMAGE_ROOTFS}/${datadir}/syslinux/isolinux.bin ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}/iso/
    install -m 0644 ${WORKDIR}/isolinux.cfg ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}/iso/
    install -m 0755 ${IMAGE_ROOTFS}/${bindir}/isohybrid ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}/iso/
    cp ${IMAGE_ROOTFS}/boot/tboot.gz ${DEPLOY_DIR_IMAGE}/
    cp ${IMAGE_ROOTFS}/boot/xen.gz ${DEPLOY_DIR_IMAGE}/
    cp ${IMAGE_ROOTFS}/boot/GM45_GS45_PM45_SINIT_51.BIN ${DEPLOY_DIR_IMAGE}/gm45.acm
    cp ${IMAGE_ROOTFS}/boot/4th_gen_i5_i7_SINIT_75.BIN ${DEPLOY_DIR_IMAGE}/hsw.acm
    cp ${IMAGE_ROOTFS}/boot/i5_i7_DUAL_SINIT_51.BIN ${DEPLOY_DIR_IMAGE}/duali.acm
    cp ${IMAGE_ROOTFS}/boot/i7_QUAD_SINIT_51.BIN ${DEPLOY_DIR_IMAGE}/quadi.acm
    cp ${IMAGE_ROOTFS}/boot/Q35_SINIT_51.BIN ${DEPLOY_DIR_IMAGE}/q35.acm
    cp ${IMAGE_ROOTFS}/boot/Q45_Q43_SINIT_51.BIN ${DEPLOY_DIR_IMAGE}/q45q43.acm
    cp ${IMAGE_ROOTFS}/boot/Xeon-5600-3500-SINIT-v1.1.bin ${DEPLOY_DIR_IMAGE}/xeon56.acm
    cp ${IMAGE_ROOTFS}/boot/Xeon-E7-8800-4800-2800-SINIT-v1.1.bin ${DEPLOY_DIR_IMAGE}/xeone7.acm
    cp ${IMAGE_ROOTFS}/boot/3rd_gen_i5_i7_SINIT_67.BIN ${DEPLOY_DIR_IMAGE}/ivb_snb.acm

    # Remove unneeded initscripts
    if [ -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/urandom ]; then
        rm -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/urandom
        update-rc.d -r ${IMAGE_ROOTFS} urandom remove
    fi
}

addtask do_post_rootfs_items after do_rootfs


OUT_DIR_INST = "${OUT_DIR_RAW}/installer"
BINARIES = "${DEPLOY_DIR}/images/${MACHINE}"

do_ship() {
	# make the output directory if it does not exist yet
	mkdir -p "${OUT_DIR_INST}"
	
	# Copy installer
        cp "${BINARIES}/${PN}-${MACHINE}.cpio.gz" "${OUT_DIR_INST}/rootfs.i686.cpio.gz"

        # Copy extra installer files
        rm -rf "${OUT_DIR_INST}/iso"
        cp -r "${BINARIES}/${PN}-${MACHINE}/iso" "${OUT_DIR_INST}/iso"
        rm -rf "${OUT_DIR_INST}/netboot"
        cp -r "${BINARIES}/${PN}-${MACHINE}/netboot" "${OUT_DIR_INST}/netboot"
        cp -f "${BINARIES}/bzImage-${MACHINE}.bin" "${OUT_DIR_INST}/bzImage"
        cp -f "${BINARIES}/xen.gz" "${OUT_DIR_INST}/xen.gz"
        cp -f "${BINARIES}/tboot.gz" "${OUT_DIR_INST}/tboot.gz"
        cp -f "${BINARIES}"/*.acm "${OUT_DIR_INST}/"
}

addtask ship before do_build after do_post_rootfs_items

python() {
    bb.data.delVarFlag("do_fetch", "noexec", d);
    bb.data.delVarFlag("do_unpack", "noexec", d);
    bb.data.delVarFlag("do_patch", "noexec", d);
    bb.data.delVarFlag("do_configure", "noexec", d);
    bb.data.delVarFlag("do_compile", "noexec", d);
    bb.data.delVarFlag("do_install", "noexec", d);
}

# image_types.bbclass fails to find init if it is a broken symlink.  Not sure
# of the proper way to fix so overriding the offending function.
IMAGE_CMD_cpio () {
	(cd ${IMAGE_ROOTFS} && find . | cpio -o -H newc >${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.cpio)
}

do_rootfs[depends] += "openxt-installer-image:do_install"
