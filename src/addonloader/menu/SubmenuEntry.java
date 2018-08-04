package addonloader.menu;

import addonloader.util.Icon;
import lejos.MainMenu;

public class SubmenuEntry extends MappedMenu implements MenuEntry {
	
	private final String title;
	private final Icon icon;
	private final boolean single_shot;
	
	public SubmenuEntry(String name, Icon icon, boolean single_shot)
	{
		this.title = name;
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
			selection = this.getSelection(selection);
			this.custom_entries.get(selection).run();
		}
		while(!single_shot && selection > -1);
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
