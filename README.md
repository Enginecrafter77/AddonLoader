# LMPluger
This project aims to improve the original LeJOS Menu.
The original menu source can be downloaded from https://sourceforge.net/projects/lejos/files/lejos-EV3/0.9.0-beta/leJOS_EV3_0.9.0-beta_source.tar.gz/download
The menu was free to modify, as it is open source. When I was using it,
I saw many possibilities that were missing in the menu. Firstly, I created
a menu with all my wanted features compiled in. The problem was there was
no abstraction at all. It was all compiled within the menu. Later, I was
inspired by the Minecraft(r) Forge(tm) and plugin loading systems. The
system loads addons as jars from specified directory. So I started to work
on a system that does something similar.

## How it works
The menu after it initializes it's essential components, it starts to
search in menu for addons (a.k.a. plugins) in specific directory. Then, it
used so-called reflection to load main class from all the jars, and launch
some method from it.

## Performance
The menu system tries to optimalize the code to the highest degree
using the best possible algorithms, while keeping code readability.
It also tries to minimize disk access and ram access and usage as well.
I try to always optimize my code, but sometimes, it is somehow hard to invent
a whole new algorithm, but the result is wonderful.

## Extensibility
The addonloader is still in development. Currently, you can add your own
menus, submenus, icons and hooks for few actions.
