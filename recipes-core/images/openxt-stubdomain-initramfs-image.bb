SUMMARY = "Stubdomain initramfs image for OpenXT"

LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://${TOPDIR}/COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe      \
                    file://${TOPDIR}/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

# initramfs image allowing to boot from location as specified on kernel
# command line, from teh choices of block device, loop back images (including
# recursive) and NFS.

COMPATIBLE_MACHINE = "(openxt-stubdomain)"

PACKAGE_ARCH = "${MACHINE_ARCH}"

PACKAGE_INSTALL = " \
		busybox \
		bridge-utils \
		initramfs-openxt \
		ioemu \
		dm-wrapper-stubdom \
		v4v-module \
		dm-agent-stubdom \
		simple-poweroff \
		"

DEPENDS += "dialog"
 
IMAGE_FSTYPES = "cpio.gz"
IMAGE_LINGUAS = ""
IMAGE_DEV_MANAGER = "busybox-mdev"
IMAGE_BOOT = "${IMAGE_DEV_MANAGER}"

inherit core-image
inherit openxt-image-src-info
inherit openxt-image-src-package

# Remove any kernel-image that the kernel-module-* packages may have pulled in.
PACKAGE_REMOVE = "kernel-image-* update-modules udev sysvinit opkg-cl"

post_rootfs_commands() {
	opkg-cl -f ${IPKGCONF_TARGET} -o ${IMAGE_ROOTFS} ${OPKG_ARGS} -force-depends remove ${PACKAGE_REMOVE};
	rm -f ${IMAGE_ROOTFS}/sbin/udhcpc;
	rm -f ${IMAGE_ROOTFS}/sbin/ldconfig;
	rm -rvf ${IMAGE_ROOTFS}/usr/lib/opkg;
}

support_vmlinuz() {
	# Make a vmlinuz link for items that explicitly reference it
	ln -sf bzImage ${IMAGE_ROOTFS}/boot/vmlinuz
}

ROOTFS_POSTPROCESS_COMMAND += " post_rootfs_commands; support_vmlinuz;"
