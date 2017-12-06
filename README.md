# rpi-guirlande


## alpine build

```
apk add --no-cache --virtual .build-utils alpine-sdk
adduser -D builder
echo "builder ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers
addgroup builder abuild
mkdir -p /var/cache/distfiles
chmod a+w /var/cache/distfiles
su - builder
# restore ~/.abuild
mkdir .abuild
scp root@arch-desktop:/home/builder/raspberrypi/image-builder/abuild/* .abuild

git clone https://github.com/vincent-tr/rpi-guirlande
cd rpi-guirlande/alpine-build
abuild checksum
abuild -r

# move package on arch-desktop
# on builder@arch-desktop
scp root@<target>:/home/builder/packages/rpi-guirlande/armhf/guirlande-1.0.0-r0.apk /home/builder/raspberrypi/image-builder/alpine-packages/armhf
```

## Test package

```
# install package
sudo apk add --allow-untrusted ~/packages/rpi-guirlande/armhf/guirlande-1.0.0-r0.apk

# install from arch-desktop
su -
scp root@arch-desktop:/home/builder/raspberrypi/image-builder/alpine-packages/armhf/guirlande-1.0.0-r0.apk .
apk add --allow-untrusted guirlande-1.0.0-r0.apk

rc-update add soft-pwm-service
rc-update add guirlande-service
rc-service soft-pwm-service start
rc-service guirlande-service start
```