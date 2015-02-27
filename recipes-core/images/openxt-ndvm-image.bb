# XenClient secure backend-domain image

include openxt-image-common.inc
IMAGE_FEATURES += "package-management"

COMPATIBLE_MACHINE = "(openxt-ndvm)"

IMAGE_FSTYPES = "xc.ext3.vhd.gz"

BAD_RECOMMENDATIONS += "avahi-daemon avahi-autoipd"
# The above seems to be broken and we *really* don't want avahi!
PACKAGE_REMOVE = "avahi-daemon avahi-autoipd hicolor-icon-theme"

export IMAGE_BASENAME = "openxt-ndvm-image"

ANGSTROM_EXTRA_INSTALL += ""

DEPENDS = "task-base"
IMAGE_INSTALL = "\
    ${ROOTFS_PKGMANAGE} \
    modules \
    task-core-boot \
    task-base \
    task-openxt-common \
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

#IMAGE_PREPROCESS_COMMAND = "create_etc_timestamp"

#zap root password for release images
ROOTFS_POSTPROCESS_COMMAND += '${@base_conditional("DISTRO_TYPE", "release", "zap_root_password; ", "",d)}'

ROOTFS_POSTPROCESS_COMMAND += "sed -i 's|root:x:0:0:root:/home/root:/bin/sh|root:x:0:0:root:/root:/bin/bash|' ${IMAGE_ROOTFS}/etc/passwd;"

ROOTFS_POSTPROCESS_COMMAND += "echo '1.0.0.0 dom0' >> ${IMAGE_ROOTFS}/etc/hosts;"

# enable ctrlaltdel reboot because PV driver uses ctrl+alt+del to interpret reboot issued via xenstore
ROOTFS_POSTPROCESS_COMMAND += "echo 'ca:12345:ctrlaltdel:/sbin/shutdown -t1 -a -r now' >> ${IMAGE_ROOTFS}/etc/inittab;"

# Move resolv.conf to /var/volatile/etc, as rootfs is readonly
ROOTFS_POSTPROCESS_COMMAND += "rm -f ${IMAGE_ROOTFS}/etc/resolv.conf; ln -s /var/volatile/etc/resolv.conf ${IMAGE_ROOTFS}/etc/resolv.conf;"

ROOTFS_POSTPROCESS_COMMAND += "opkg-cl ${IPKG_ARGS} -force-depends \
                                remove ${PACKAGE_REMOVE};"

inherit selinux-image
#inherit validate-package-versions
inherit openxt-image-src-info
inherit openxt-image-src-package
inherit openxt-licences
require openxt-version.inc

LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6      \
                    file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
