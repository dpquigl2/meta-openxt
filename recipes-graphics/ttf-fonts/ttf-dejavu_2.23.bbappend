pkg_postinst() {
    if [ -n "$D" ]; then
        exit 0 
    fi
}
