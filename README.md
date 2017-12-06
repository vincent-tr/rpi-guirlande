# rpi-guirlande

## alpine setup

```
apk add git make gcc musl-dev
git clone https://github.com/vincent-tr/rpi-guirlande
cd rpi-guirlande
fakeroot -- apk -p /tmp/root-fs add --initdb --no-scripts --update-cache alpine-base linux-rpi-dev linux-rpi2-dev --arch armhf --keys-dir /etc/apk/keys --repositories-file /etc/apk/repositories
mkdir build
cd soft-pwm
make KERNEL_BUILD=/tmp/root-fs/usr/src/linux-headers-4.9.65-0-rpi
mv soft_pwm.ko ../build/soft_pwm.rpi1.ko
make KERNEL_BUILD=/tmp/root-fs/usr/src/linux-headers-4.9.65-0-rpi clean
make KERNEL_BUILD=/tmp/root-fs/usr/src/linux-headers-4.9.65-0-rpi2
mv soft_pwm.ko ../build/soft_pwm.rpi2.ko
make KERNEL_BUILD=/tmp/root-fs/usr/src/linux-headers-4.9.65-0-rpi2 clean
cd ..
rm -rf /tmp/root-fs
cd guirlande-service
make
mv guirlande-service ../build
make clean
cd ..
```

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