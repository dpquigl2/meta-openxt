# This image is used to build the XenClient source iso.
#
# Installs the source package for every recipe which needs to be built in
# order to build the other XenClient image recipes.

export IMAGE_BASENAME = "openxt-source-image"

# The following list must be updated each time a new image is added:
PACKAGE_INSTALL = "\
    openxt-initramfs-image-sources \
    openxt-dom0-image-sources \
    openxt-uivm-image-sources \
    openxt-ndvm-image-sources \
    openxt-syncvm-image-sources \
    openxt-installer-image-sources \
    openxt-installer-part2-image-sources"

IMAGE_BOOT = ""
IMAGE_FSTYPES = "raw"
IMAGE_INSTALL = ""
IMAGE_LINGUAS = ""
ONLINE_PACKAGE_MANAGEMENT = "none"

inherit image

ROOTFS_POSTPROCESS_COMMAND =+ " \
    rm -rf ${IMAGE_ROOTFS}/dev; \
    rm -rf ${IMAGE_ROOTFS}/etc; \
    rm -rf ${IMAGE_ROOTFS}/usr; \
    rm -rf ${IMAGE_ROOTFS}/Proprietary; \
    rm -rf ${IMAGE_ROOTFS}/[Uu]nknown; \
"

LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6      \
                    file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# prevent ldconfig from being run
LDCONFIGDEPEND = ""
