ccemux.attach("left", "tm_gpu")

local gpu = peripheral.wrap("left")
gpu.refreshSize()
gpu.sync()

win = gpu.createWindow(1, 1, 1280, 720)
win.fill(0x171717)
win.sync()
win.setFont("ascii")
win.drawText(1, 1, "Hello World", 0xFF0000, 0x171717, 14)
win.sync()

