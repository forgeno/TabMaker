import java.awt.Color;
import java.awt.Graphics2D;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;

@ScriptManifest(author = "Azubu", info = "Smart tab maker that requires user to start by Edgeville house portal", logo = "https://i.imgur.com/Ak96Qn3.png", name = "Azubu Smart Tabber", version = 0.1)
public class MainClass extends Script{
	public GuiClass mainGui = new GuiClass();
	//Initalize interface for displaying graphics and data
	/*
	private BufferedImage backgroundGUI;
	private String bgDir = String.format("%s/OSBot/Data/TabMakerGUI.png",System.getProperty("user.home"));
	private File f = new File(bgDir);
	*/
	//finish initalizing interface
	private long startTime;//initalize variable to store time of program
	String[] hostNames;
	String currentHost = "";
	int currentHostPosition = 0; 
	@Override
	public void onStart() throws InterruptedException {
		mainGui.launchGui(this);
		/*
		try{
            backgroundGUI = ImageIO.read(new File(bgDir));
            log("Successfully loaded image locally");
        } 
		catch(IOException e){
			log(e);
        }
        */
		getExperienceTracker().start(Skill.MAGIC);
		startTime = System.currentTimeMillis();
	}
	
	@Override
	public int onLoop() throws InterruptedException {
		//log("Tab selected: "+mainGui.getTabString());
		//log("current host name: "+currentHost);
		//log("Gui Active? : "+mainGui.isActive());
		if(mainGui.isActive()) {//loop to wait for GUI to close
			
		}
		else {//Code when GUI is closed
			if(currentHost.equals("")) {
				hostNames = mainGui.getHostString().split(",");
				currentHost = hostNames[0];
			}
			if(checkResources()) {
				log("Ran out of resources, stopping script...");
				stop(false);
			}
			//log("current host: "+currentHost);
			if(getWidgets().isVisible(229,1) && getWidgets().get(229, 1) != null) {
				log("House owner logged out, finding new host...");
				getNewHost();
			}
			if(getNpcs().closest("Phials") != null && getInventory().getEmptySlotCount() != 0 && getWidgets().get(162, 46).isVisible()) {
				interactPhials();
			}
			if(objects.closest(15478) != null && getInventory().getEmptySlotCount() == 0) {
				enterHouse();
			}
			//log("1: "+(objects.closest(4525) != null)+" 2. "+(myPlayer().getAnimation() == -1)+" 3. "+(myPlayer().isOnScreen())+" 4. "+(getPlayers().closest(currentHost.replace(' ', '\u00A0')) != null));
			if(objects.closest(4525) != null && myPlayer().getAnimation() == -1 && myPlayer().isOnScreen() && getPlayers().closest(currentHost.replace(' ', '\u00A0')) != null && getInventory().contains(1761)) {
				//log("Finding lectern entered");
				findLectern();
			}
			if(getWidgets().get(79, 0) != null && getWidgets().isVisible(79, 0)) {
				makeTabs();
			}
			if((myPlayer().getAnimation() != 4068) && !(inventory.contains(1761)) && objects.closest(4525) != null) {
				exitHouse();
			}
		}
		return random(300,800);
	}
	
	@Override
	public void onExit() throws InterruptedException {
		
	}
	
	@Override
	public void onPaint(Graphics2D g){
		g.setColor(new Color(155,191,225,50));
		g.fillRect(0, 240, 250, 95);
		Graphics2D textUI = (Graphics2D) g.create();
		g.setColor(new Color(250,207,190));
		textUI.drawString("Azubu SmartTabber 1.0", 5, 255);
		textUI.drawString("Total runtime: "+formatTime(System.currentTimeMillis() - startTime),5,270);
		textUI.drawString("Magic XP gained: "+Double.toString(getExperienceTracker().getGainedXP(Skill.MAGIC)),5,285);
		textUI.drawString("Magic XP/h: "+getExperienceTracker().getGainedXPPerHour(Skill.MAGIC), 5, 300);
		/*
		if(backgroundGUI != null) {
			g.drawImage(backgroundGUI, null,0,338);
		}*/
		/*
		g.drawString("Total runtime: "+formatTime(System.currentTimeMillis() - startTime),210,370);
		g.drawString("Magic XP gained: "+Double.toString(getExperienceTracker().getGainedXP(Skill.MAGIC)),210,400);
		g.drawString("Time till levelup: "+formatTime(getExperienceTracker().getTimeToLevel(Skill.MAGIC)),210,415);
		g.drawString("XP/H: "+getExperienceTracker().getGainedXPPerHour(Skill.MAGIC), 270, 430);
		*/
		
	}
	public final String formatTime(final long ms){
	    long s = ms / 1000, m = s / 60, h = m / 60;
	    s %= 60; m %= 60; h %= 24;
	    return String.format("%02d:%02d:%02d", h, m, s);
	}
	public void interactPhials() {
		log("Locating Phials");
		@SuppressWarnings("unchecked")
		NPC Phials = getNpcs().closest(new Filter<NPC>() {
			@Override
			public boolean match(NPC npc) {
				return npc != null && npc.getName().equals("Phials") && npc.exists();
		}});
		if(map.canReach(Phials)) {
			log("Found Phials, unnoting clay");
			getWalking().walk(Phials);
		}

		new ConditionalSleep(10000) {
			@Override
			public boolean condition() throws InterruptedException {
				// TODO Auto-generated method stub
				return (Phials.getPosition().distance(myPlayer().getPosition())<3 && Phials.isVisible());
			}
		};
		if(getInventory() != null && getInventory().contains(1762)) {
			log("accessing use command");
			getInventory().interact("use", 1762);
			Phials.interact("use");
		}
		new ConditionalSleep(10000) {
			@Override
			public boolean condition() throws InterruptedException {
				// TODO Auto-generated method stub
				return (getWidgets().isVisible(219,0,1));
			}
		}.sleep();
		if(getInventory().getEmptySlotCount() == 1) {
			//dialogues.selectOption(1);
			getWidgets().get(219, 0, 1).interact();
		}
		if(getInventory().getEmptySlotCount() > 1 && getInventory().getEmptySlotCount() < 6) {
			getWidgets().get(219, 0, 2).interact();	
			//dialogues.selectOption(2);
				}
		if(getInventory().getEmptySlotCount() >= 6) {
			getWidgets().get(219, 0, 3).interact();
			//log("trying to select option 3: "+getWidgets().get(219,0,1).getMessage());
			//dialogues.selectOption(3);
		}
		log("Current empty slots: "+getInventory().getEmptySlotCount());
		new ConditionalSleep(10000) {
			@Override
			public boolean condition() throws InterruptedException {
				// TODO Auto-generated method stub
				return (getWidgets().isVisible(193,0));
			}
		};
		log("Done unnoting clay");
	}
	
	public void getNewHost(){
		log("Current Host: "+currentHost);
		//log("mainGui.getHostString(): "+mainGui.getHostString());
		if(currentHostPosition < hostNames.length-1) {
			currentHostPosition++;
			currentHost = hostNames[currentHostPosition];
			log("New host is: "+currentHost+" "+(currentHostPosition+1)+"/"+hostNames.length);
		}
		else {
			log("No other hosts inputted. Stopping script");
			stop(false);
		}
	}
	public void enterHouse() throws InterruptedException {
		RS2Object portalEnter = objects.closest("Portal");
		getWalking().walk(portalEnter);
		if(!currentHost.equals("")) {
			portalEnter.interact("Friend's house");
			new ConditionalSleep(5000) {
				@Override
				public boolean condition() throws InterruptedException {
					// TODO Auto-generated method stub
					return getWidgets().isVisible(162,37) && getWidgets().get(162,37) != null;
				}
			}.sleep();
			sleep(random(1000,1500));
			//log("Widget string extracted: "+getWidgets().get(162, 33, 0).getMessage());
			//log("Match?: "+getWidgets().get(162, 33, 0).getMessage().equals("<col=000000>Last name:</col> "+currentHost));
			if(getWidgets().isVisible(162, 33, 0) && getWidgets().get(162, 33, 0) != null && getWidgets().get(162, 33, 0).getMessage().equals("<col=000000>Last name:</col> "+currentHost)) {
				getWidgets().get(162, 33, 0).interact();
			}
			else if(getWidgets().isVisible(162, 37) && getWidgets().get(162, 37) != null){
				//log("CurrentHost2: "+currentHost);
				sleep(random(1500,2200));
				keyboard.typeString(currentHost);
				keyboard.typeEnter();
			}
			log("Waiting to arrive inside house");
			new ConditionalSleep(10000) {
				@Override
				public boolean condition() throws InterruptedException {
					// TODO Auto-generated method stub
					return myPlayer().isVisible() && objects.closest("Portal").isVisible();
				}
			}.sleep();
			sleep(random(3500,4500));
			log("Arrived inside house");
			
		}
		else {
			portalEnter.interact("Home");
		}
	}
	
	public void findLectern() {
		RS2Object Lectern = null;
		if(mainGui.getTabString().equals("Bones to Peaches") || mainGui.getTabString().equals("Bones to Bananas")) {
			if(objects.closest(13648) != null) {
				Lectern = objects.closest(13648);
				//log("Looking for demon lectern");
				if(Lectern.exists()) {
					log("walking to demon lectern");
					getWalking().walk(Lectern);
					if(Lectern.isVisible() && myPlayer().getAnimation() == -1) {
						Lectern.interact("Study");
						log("Studying demon lectern");
					}
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					new ConditionalSleep(10000) {
						@Override
						public boolean condition() throws InterruptedException {
							return getWidgets().get(79, 0) != null;
						}
					}.sleep();
				}
			}
			
			else{
				getWalking().walk(objects.closest(4525));
				objects.closest(4525).interact("enter");
				log("Cannot find Demon lectern in house, leaving house");
				try {
					sleep(random(2000,3000));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getNewHost();
				return;
			}	
		}
		else {
			if(objects.closest(13647) != null) {
				Lectern = objects.closest(13647);;
				log("Looking for eagle lectern");
				if(Lectern.exists()) {
					log("walking to eagle lectern");
					getWalking().walk(Lectern);
					if(Lectern.isVisible() && myPlayer().getAnimation() == -1) {
						Lectern.interact("Study");
						log("Studying eagle lectern");
					}
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					new ConditionalSleep(10000) {
						@Override
						public boolean condition() throws InterruptedException {
							return getWidgets().get(79, 0) != null;
						}
					}.sleep();
				}
			}
			
			else {
				getWalking().walk(objects.closest(4525));
				objects.closest(4525).interact("enter");
				log("Cannot find Eagle lectern in house, leaving house");
				try {
					sleep(random(2000,3000));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getNewHost();
				return;
			}
		}	
	}
	
	public void makeTabs() {
		RS2Widget tabWidget = null;
		log("Making "+mainGui.getTabString()+" tele tabs");
		if(mainGui.getTabString().equals("House")) {
			tabWidget = getWidgets().get(79, 17);
		}
		else if(mainGui.getTabString().equals("Lumbridge")) {
			tabWidget = getWidgets().get(79, 12);
		}
		else if(mainGui.getTabString().equals("Varrock")) {
			tabWidget = getWidgets().get(79, 11);
		}
		else if(mainGui.getTabString().equals("Falador")) {
			tabWidget = getWidgets().get(79, 13);
		}
		else if(mainGui.getTabString().equals("Camelot")) {
			tabWidget = getWidgets().get(79, 14);
		}
		else if(mainGui.getTabString().equals("Ardougne")) {
			tabWidget = getWidgets().get(79, 15);
		}
		else if(mainGui.getTabString().equals("Watchtower")) {
			tabWidget = getWidgets().get(79, 16);
		}
		else if(mainGui.getTabString().equals("Bones to Peaches")) {
			tabWidget = getWidgets().get(79, 10);
		}
		else if(mainGui.getTabString().equals("Bones to Bananas")) {
			tabWidget = getWidgets().get(79, 5);
		}
		tabWidget.hover();
		mouse.click(true);
		new ConditionalSleep(10000) {
			@Override
			public boolean condition() throws InterruptedException {
				return menu.isOpen();
			}
		};
		
		if(menu.isOpen()) {
			menu.selectAction("Make-All");
			try {
				sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new ConditionalSleep(60000) {
				@Override
				public boolean condition() throws InterruptedException {
					//log(!(myPlayer().getAnimation() == 4068)+" "+!(inventory.contains(1761))+" "+dialogues.isPendingContinuation());
					return (!(myPlayer().getAnimation() == 4068) && !(inventory.contains(1761)) || dialogues.isPendingContinuation());
				}
			}.sleep();
		}
	}
	
	public void exitHouse() {
		RS2Object exitPortal = objects.closest(4525);
		int smartExit = random(0,1);
		log("Exiting house");
		if(smartExit == 1 && exitPortal.isVisible()) {
			exitPortal.interact("enter");
		}
		else{
			getWalking().walk(exitPortal);
			exitPortal.interact("enter");
		}
		try {
			sleep(random(1700,2200));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Boolean checkResources() {
		if(!getInventory().contains(1762,563,995)) {
			return true;	
		}
		return false;
	}
	
	public void onMessage(Message message) throws java.lang.InterruptedException {
		String text = message.getMessage().toLowerCase();
		if (text.contains("that player is offline, or has privacy mode enabled.") && message.getType() == Message.MessageType.GAME || text.contains("they have locked their house to visitors.") && message.getType() == Message.MessageType.GAME)  {
			log("Detected host house down, checking for alternatives");
			getNewHost();
		}
	}
}
