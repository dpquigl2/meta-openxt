# XenClient sysroot image
LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://${TOPDIR}/COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe      \
                    file://${TOPDIR}/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

include openxt-image-common.inc

COMPATIBLE_MACHINE = "(openxt-dom0)"

IMAGE_FSTYPES = "cpio.bz2"

# No thanks, we provide our own xorg.conf with the hacked Intel driver
# And we don't need Avahi
BAD_RECOMMENDATIONS += "xserver-xorg avahi-daemon avahi-autoipd"
# The above seems to be broken and we *really* don't want avahi!
PACKAGE_REMOVE = "avahi-daemon avahi-autoipd"

ANGSTROM_EXTRA_INSTALL += " \
			  " 
export IMAGE_BASENAME = "openxt-sysroot-image"
export STAGING_KERNEL_DIR

FRIENDLY_NAME = "sysroot"

DEPENDS = "packagegroup-base packagegroup-openxt-dom0 packagegroup-openxt-dom0-extra"
IMAGE_INSTALL = "\
    ${ROOTFS_PKGMANAGE} \
    initscripts \
    modules \
    packagegroup-base \
    packagegroup-core-boot \
    packagegroup-openxt-common \
    packagegroup-openxt-dom0 \
    essential-target-builddepends \
    ${ANGSTROM_EXTRA_INSTALL}"

#IMAGE_PREPROCESS_COMMAND = "create_etc_timestamp"

inherit image openxt
#inherit validate-package-versions
inherit openxt-image-src-info
inherit openxt-image-src-package
inherit openxt-licences
require openxt-version.inc

do_post_rootfs_commands() {
	sed -i 's|root:x:0:0:root:/home/root:/bin/sh|root:x:0:0:root:/root:/bin/bash|' ${IMAGE_ROOTFS}/etc/passwd;

	rm ${IMAGE_ROOTFS}/etc/hosts;
	ln -s /tmp/hosts ${IMAGE_ROOTFS}/etc/hosts;

	# Add initramfs
	cat ${DEPLOY_DIR_IMAGE}/openxt-initramfs-image-openxt-dom0.cpio.gz > ${IMAGE_ROOTFS}/boot/initramfs.gz;

	sed -i 's|1:2345:respawn:/sbin/getty 38400 tty1|#1:2345:respawn:/sbin/getty 38400 tty1|' ${IMAGE_ROOTFS}/etc/inittab;

	# Add input demon to inittab (temp hack)
	echo 'xi:5:respawn:/usr/bin/input_server >/dev/null 2>&1' >> ${IMAGE_ROOTFS}/etc/inittab;

	# Same with surfman
	echo 'xs:5:respawn:/usr/bin/watch_surfman >/dev/null 2>&1' >> ${IMAGE_ROOTFS}/etc/inittab;

	# Add dom0 console getty
	echo '1:2345:respawn:/sbin/getty 38400 tty1' >> ${IMAGE_ROOTFS}/etc/inittab;

	# Create mountpoint for /mnt/secure
	mkdir -p ${IMAGE_ROOTFS}/mnt/secure;

	# Create mountpoint for boot/system
	mkdir -p ${IMAGE_ROOTFS}/boot/system;

	# Remove unwanted packages specified above
	opkg-cl -f ${IPKGCONF_TARGET} -o ${IMAGE_ROOTFS} ${OPKG_ARGS} -force-depends remove ${PACKAGE_REMOVE};

	# Write coredumps in /var/cores
	echo 'kernel.core_pattern = /var/cores/%e-%t.%p.core' >> ${IMAGE_ROOTFS}/etc/sysctl.conf;
}

# Get rid of unneeded initscripts
remove_initscripts() {
    if [ -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/hostname.sh ]; then
        rm -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/hostname.sh
        update-rc.d -r ${IMAGE_ROOTFS} hostname.sh remove
    fi

    if [ -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/rmnologin.sh ]; then
        rm -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/rmnologin.sh
        update-rc.d -r ${IMAGE_ROOTFS} rmnologin.sh remove
    fi

    if [ -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/finish.sh ]; then
        rm -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/finish.sh
        update-rc.d -r ${IMAGE_ROOTFS} finish.sh remove
    fi

    if [ -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/mount-special ]; then
        rm -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/mount-special
        update-rc.d -r ${IMAGE_ROOTFS} mount-special remove
    fi
}

# Symlink /root to /home/root until nothing references /root anymore, e.g. SELinux file_contexts
link_root_dir() {
    ln -sf /home/root ${IMAGE_ROOTFS}/root
}

#zap root password for release images
ROOTFS_POSTPROCESS_COMMAND += '${@base_conditional("DISTRO_TYPE", "release", "zap_root_password; ", "",d)}'

ROOTFS_POSTPROCESS_COMMAND += ' remove_initscripts; link_root_dir;'

addtask do_post_rootfs_commands after do_rootfs
addtask ship before do_build after do_rootfs

