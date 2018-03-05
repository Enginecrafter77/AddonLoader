# LMPlugger (leJOS EV3 Modular Menu)
LMPlugger is an improvement of the original leJOS Menu.
The original menu source can be downloaded from https://sourceforge.net/projects/lejos/files/lejos-EV3/0.9.0-beta/leJOS_EV3_0.9.0-beta_source.tar.gz/download

## History
The menu was free to modify, as it is open source. When I was using it,
I saw many possibilities that were missing in the menu. Firstly, I created
a menu with all my wanted features compiled in. The problem was that if I wanted
to add new features alongside the old ones, the whole source code
was needed. Later, I got inspired by the Minecraft(r) Forge(tm) mod loading system.
The system loads addons as jars from specified directory. So I started to work
on a system that does something similar.

## Installation
You are supposed to have working leJOS EV3 eclipse development environment.

<dl>
	<dt>Step 1:</dt>
	<dd>Download (or clone) the repository inside your computer.</dd>	
	<dt>Step 2:</dt>
	<dd>Open the project in Eclipse.  
	Click File -> Import -> Existing Projects into Workspace  
	Then Click Browse, and select your copy of this repository.  
	And, click Finish. The repoistory shall be loaded into your eclipse workspace.</dd>  
	<dt>Step 3:</dt>
	<dd>Compile & Upload the code. This step is very simple. Just click Run (the green circle with arrow).</dd>
	<dt>Step 4:</dt>
	<dd>The menu will install itself. When the operation is complete, it temporarily returns to the main menu. Then wait, and it reboots itself.</dd>
</dl>

The installer was tested to work on new system.  
If the installation does not work, open a new issue. It will be fixed as soon as possible.

## How it works
The menu after it initializes it's essential components, it starts to
search in menu for addons in specific directory. Then, it
uses java reflection to load main class from all the jars.
For every jar, the addonloader searches for main class (specifically annotated),
and that class will be instantiated and launched.

## Status
The project is still in active development, and many things need to be improved.
Currently, you can add your own menus, submenus, menu entries, icons and simple hooks for few actions.