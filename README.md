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

## Future works [WIP]
+ Add more configuration options using XML
+ Add more abstraction to add extensibility
