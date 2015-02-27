# Part 2 of the XenClient host installer.
#
# This contains the logic to install or upgrade a specific version of
# XenClient. The resulting image is copied to the control.tar.bz2 file
# in the XenClient repository.

COMPATIBLE_MACHINE = "(openxt-dom0)"

export IMAGE_BASENAME = "openxt-installer-part2-image"

PACKAGE_INSTALL = "openxt-installer-part2"

IMAGE_BOOT = ""
IMAGE_FSTYPES = "tar.bz2"
IMAGE_INSTALL = ""
IMAGE_LINGUAS = ""
ONLINE_PACKAGE_MANAGEMENT = "none"

inherit image
inherit openxt-image-src-info
inherit openxt-image-src-package
inherit openxt-licences
require openxt-version.inc

ROOTFS_POSTPROCESS_COMMAND += " \
    mv ${IMAGE_ROOTFS}/etc/openxt.conf ${IMAGE_ROOTFS}/config/; \
    rm -rf ${IMAGE_ROOTFS}/dev; \
    rm -rf ${IMAGE_ROOTFS}/etc; \
    rm -rf ${IMAGE_ROOTFS}/usr;"

LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6      \
                    file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# prevent ldconfig from being run
LDCONFIGDEPEND = ""
