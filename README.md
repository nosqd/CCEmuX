# CCEmuX (nosqd fork)
A new open source CC emulator, written in Java.

## New Features

- [x] Tom's Peripherals GPUImpl
- [x] Tom's Peripherals GPU3D
- [ ] Void Power Hologram

NOTICE: All of my code, was written for AWT, so if you change your renderer to JFX or TRoR it will still use AWT for
Tom's Peripherals Monitor and for Void Power Hologram

NOTICE2: I know that I can develop my own plugin instead of making builtin plugin, I will transition to
using plugin system in the future, for now it is easier to develop inside of CCEmuX

## How to use with Tom's Peripherals Emulation

You just attach new peripheral of type `tm_gpu`, like this.

```lua
ccemux.attach("gpu", "tm_gpu")
```

And use it.

### New methods for Tom's Peripherals

#### GPUImpl.setMonRes
Arguments: `countX: number, countY: number`

Return Value: `(none)`

Description: 
Sets virtual count of monitors for gpu, so if use use `gpu.setMonRes(4,3)` it will emulate gpu with 4x3 monitors
connected but it is true only for raw gpu usage, when you create window it will not work.

```lua
gpu.setMonRes(w,h)
```

#### GPUImpl.createGpuView
Arguments: `(none)`

Return Value: `(none)`

Description:
By default raw gpu view is disabled, you can show it with this function, but remember, that raw gpu view is scaled by 8, so
if you want to do something high-res you should use `createWindow` and `createWindow3d`

```lua
gpu.createGpuView(w,h)
```

## Building
As simple as running `./gradlew build`. The compiled, runnable jar will be written to `build/libs/CCEmuX-version-all.jar`.
