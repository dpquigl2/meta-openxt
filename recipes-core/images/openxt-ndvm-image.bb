# XenClient secure backend-domain image
LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://${TOPDIR}/COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe      \
                    file://${TOPDIR}/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

include openxt-image-common.inc
IMAGE_FEATURES += "package-management"

COMPATIBLE_MACHINE = "(openxt-ndvm)"

IMAGE_FSTYPES = "xc.ext3.vhd.gz"

#IMAGE_LINGUAS = ""

FRIENDLY_NAME = "ndvm"

BAD_RECOMMENDATIONS += "avahi-daemon avahi-autoipd"
# The above seems to be broken and we *really* don't want avahi!
PACKAGE_REMOVE = "avahi-daemon avahi-autoipd hicolor-icon-theme"

export IMAGE_BASENAME = "openxt-ndvm-image"

ANGSTROM_EXTRA_INSTALL += ""

DEPENDS = "packagegroup-base"
IMAGE_INSTALL = "\
    ${ROOTFS_PKGMANAGE} \
    modules \
    packagegroup-core-boot \
    packagegroup-base \
    packagegroup-openxt-common \
    util-linux-mount \
    util-linux-umount \
    busybox \
    openssh \
    kernel-modules \
    libv4v \
    libv4v-bin \
    dbus \
    openxt-dbusbouncer \
    networkmanager \
    openxt-toolstack \
    intel-e1000e \
    intel-e1000e-conf \
    linux-firmware \
    rt2870-firmware \
    rt3572 \
    bridge-utils \
    iptables \
    openxt-ndvm-tweaks \
    ipsec-tools \
    rsyslog \
    ${ANGSTROM_EXTRA_INSTALL} \
    openxt-udev-force-discreet-net-to-eth0 \
    v4v-module \
    xen-tools-libxenstore \
    xen-tools-xenstore-utils \
    wget \
    ethtool \
    carrier-detect \
    openxt-nws \
    modemmanager \
    ppp \
    iputils-ping \
"

# Packages disabled for Linux3 to be fixed
# rt5370

inherit selinux-image openxt
#inherit validate-package-versions
inherit openxt-image-src-info
inherit openxt-image-src-package
inherit openxt-licences
require openxt-version.inc

#IMAGE_PREPROCESS_COMMAND = "create_etc_timestamp"

#zap root password for release images
ROOTFS_POSTPROCESS_COMMAND += '${@base_conditional("DISTRO_TYPE", "release", "zap_root_password; ", "",d)}'

tweak_passwd() {
	sed -i 's|root:x:0:0:root:/home/root:/bin/sh|root:x:0:0:root:/root:/bin/bash|' ${IMAGE_ROOTFS}/etc/passwd;
}

tweak_hosts() {
	echo '1.0.0.0 dom0' >> ${IMAGE_ROOTFS}/etc/hosts;
}

# enable ctrlaltdel reboot because PV driver uses ctrl+alt+del to interpret reboot issued via xenstore
enable_three_fingered_salute() {
	echo 'ca:12345:ctrlaltdel:/sbin/shutdown -t1 -a -r now' >> ${IMAGE_ROOTFS}/etc/inittab;
}

# Move resolv.conf to /var/volatile/etc, as rootfs is readonly
relocate_resolv() {
	rm -f ${IMAGE_ROOTFS}/etc/resolv.conf;
	ln -s /var/volatile/etc/resolv.conf ${IMAGE_ROOTFS}/etc/resolv.conf;
}

remove_unwanted_packages() {
	opkg-cl -f ${IPKGCONF_TARGET} -o ${IMAGE_ROOTFS} ${OPKG_ARGS} -force-depends remove ${PACKAGE_REMOVE};
}

remove_initscripts() {
    # Remove unneeded initscripts
    if [ -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/urandom ]; then
        rm -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/urandom
        update-rc.d -r ${IMAGE_ROOTFS} urandom remove
    fi
    if [ -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/sshd ]; then
        rm -f ${IMAGE_ROOTFS}${sysconfdir}/init.d/sshd
        update-rc.d -r ${IMAGE_ROOTFS} sshd remove
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

ROOTFS_POSTPROCESS_COMMAND += "tweak_passwd; tweak_hosts; enable_three_fingered_salute; relocate_resolv; remove_unwanted_packages; remove_initscripts; support_vmlinuz; link_root_dir;"

addtask ship before do_build after do_rootfs
