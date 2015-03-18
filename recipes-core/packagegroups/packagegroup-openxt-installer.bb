DESCRIPTION = "All packages required for XenClient installer"
LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://${TOPDIR}/COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe      \
                    file://${TOPDIR}/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
PR = "r11"

inherit packagegroup

RDEPENDS_${PN} = " \
    kernel-modules \
    openssh \
    openssh-sshd-tcp-init \
    util-linux-mount \
    util-linux-umount \
    blktap \
    dialog \
    e2fsprogs \
    e2fsprogs-e2fsck \
    e2fsprogs-mke2fs \
    e2fsprogs-tune2fs \
    eject \
    lvm2 \
    util-linux-sfdisk \
    xen \
    xen-tools \
    openxt-installer \
    openxt-installer-tweaks \
    cryptsetup \
    openxt-get-config-key \
    falloc \
    openssl \
    openxt-toolstack \
    read-edid \
    pciutils-ids \
    dosfstools \
    syslinux \
    syslinux-isohybrid \
    syslinux-isolinux \
    syslinux-mboot \
    syslinux-pxelinux \
    squashfs-tools \
    ethtool \
    rsyslog \
    openxt-udev-force-discreet-net-to-eth0 \
    bc \
    wget \
    refpolicy-mcs \
    dmidecode \
    netcat \
    tboot \
    packagegroup-tboot \
    trousers \
    trousers-data \
    tpm-tools \
    openxt-pcrdiff \
    openxt-tpm-scripts \
    ncurses \
"

# open-iscsi-user \
# 
