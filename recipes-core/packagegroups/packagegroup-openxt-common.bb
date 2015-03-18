DESCRIPTION = "Common packages for XenClient images"
LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://${TOPDIR}/COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe      \
                    file://${TOPDIR}/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit openxt
inherit packagegroup

# depend on shadow-native, to make sure that pwconv is found during
# rootfs construction as shadow for various reasons does not depend on it
# and other packages may or may not pull it
# when removing shadow dependecy please remove also shadow-native
DEPENDS += "shadow-native"

RDEPENDS_${PN} = " \
    openxt-feed-configs \
    shadow \
    bash \
    bzip2 \
    coreutils \
    gzip \
    ldd \
    less \
    procps \
    rsync \
    strace \
    vim \
    sysvinit-pidof \
    nano \
"

# How to get vim-tiny now.
PACKAGECONFIG_append_vim = " tiny"
