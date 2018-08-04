package addonloader.menu;

import addonloader.util.ui.Icon;
import lejos.MainMenu;

public class SubmenuEntry extends MappedMenu implements MenuEntry {
	
	private static final long serialVersionUID = 3254268740010579131L;
	
	private final Icon icon;
	private final boolean single_shot;
	
	public SubmenuEntry(String name, Icon icon, boolean single_shot)
	{
		super(name);
		this.icon = icon;
		this.single_shot = single_shot;
	}
	
	public SubmenuEntry(String name, Icon icon)
	{
		this(name, icon, false);
	}
	
	@Override
	public void run()
	{
		int selection = 0;
		do
		{
			MainMenu.self.newScreen(this.title);
			selection = this.open();
			if(selection < 0) break;
			this.get(selection).run();
		}
		while(!single_shot);
	}

	@Override
	public Icon getIcon()
	{
		return this.icon;
	}

	@Override
	public String getName()
	{
		return this.title;
	}

}
