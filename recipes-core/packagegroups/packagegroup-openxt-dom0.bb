DESCRIPTION = "All packages required for XenClient dom0"
LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://${TOPDIR}/COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe      \
                    file://${TOPDIR}/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit openxt
inherit packagegroup

RDEPENDS_${PN} = " \
    openssh \
    openssh-sshd-tcp-init \
    util-linux-mount \
    util-linux-umount \
    xen \
    xen-tools \
    xen-firmware \
    xen-xsm-policy \
    grub \
    tboot \
    e2fsprogs-tune2fs \
    kernel-modules \
    libv4v \
    libv4v-bin \
    libedid \
    libxenacpi \
    lvm2 \
    bridge-utils \
    iptables \
    iproute2 \
    ioemu \
    xcpmd \
    pmutil \
    vbetool-xc \
    openxt-toolstack \
    openxt-input-daemon \
    openxt-dom0-tweaks \
    openxt-splash-images \
    openxt-config-access \
    openxt-cryptdisks \
    cryptsetup \
    openxt-get-config-key \
    openxt-root-ro \
    alsa-utils-alsactl \
    alsa-utils-alsaconf \
    alsa-utils-alsamixer \
    openxt-boot-sound \
    curl \
    trousers \
    trousers-data \
    tpm-tools \
    tpm-tools-sa \
    openxt-tpm-setup \
    squashfs-tools \
    pciutils-ids \
    packagegroup-tboot \
    read-edid \
    openssl \
    ntpdate \
    dd-buffered \
    vhd-copy \
    secure-vm \
    openxt-sec-scripts \
    pmtools \
    xenaccess \
    blktap \
    svirt-interpose \
    selinux-load \
    ustr \
    ethtool \
    bootage \
    microcode-ctl \
    intel-microcode \
    rsyslog \
    logrotate \
    qemu-wrappers \
    dialog \
    openxt-udev-force-discreet-net-to-eth0 \
    openxt-nwd \
    wget \
    xen-tools-xenstored \
    xen-tools-xenconsoled \
    openxt-repo-certs \
    gobi-loader \
    usb-modeswitch \
    upgrade-db \
    rpc-proxy \
    dbd \
    openxt-language-sync \
    pci-dm-helper \
    atapi-pt-helper \
    audio-helper \
    compleat \
    xec \
    apptool \
    dmidecode \
    netcat \
    audio-daemon \
    linux-firmware \
    libicbinn-server \
    monit \
    upower \
    screen \
    openxt-pcrdiff \
    drm-surfman-plugin \
    eject \
    linux-input \
    iputils-ping \
    vusb-daemon \
    xenmgr-data \
    updatemgr \
    uid \
    surfman \
    linuxfb-surfman-plugin \
    dm-agent \
    xenmgr \
"

# OE upgrade - temporarly disabled:

# gconf-dbus \
# xserver-xorg \
# xf86-video-intel-openxt-dom0 \
# xf86-video-vesa-openxt-dom0 \
#
