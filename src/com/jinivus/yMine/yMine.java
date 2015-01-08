
package com.jinivus.yMine;

import com.jinivus.yMine.tools.Data;
import com.jinivus.yMine.tools.Util;
import org.powerbot.script.*;
import org.powerbot.script.rt6.Constants;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;


@Script.Manifest(name = "yMine", description = "Yanille Iron Miner")



public class yMine extends PollingScript<ClientContext> implements MessageListener, PaintListener {



	public  static Data data;
	private int oresMined;
	private int startXP = -1;
	private long startTime;

	private BufferedImage paintBackground;

	private final Font font = new Font("Georgia",1,16);


	private enum STATE {
		BANKING, MINING, WALKINGTOMINE, WALKINGTOBANK, IDLE
	}


	@Override
	public void start() {
		oresMined = 0;
		data = new Data();
		if(ctx.game.loggedIn())
			startXP = ctx.skills.experience(Constants.SKILLS_MINING);
		startTime = System.currentTimeMillis();
		paintBackground = downloadImage("http://i.imgur.com/AXwf2qO.png");
	}


	private STATE state() {
		ctx.backpack.select();
		final Area bankArea = yMine.data.getBankArea();
		if (ctx.backpack.select().count() == 28 && !bankArea.contains(ctx.players.local().tile()))
			return STATE.WALKINGTOBANK;
		if (ctx.backpack.select().count()  == 28 && bankArea.contains(ctx.players.local().tile()))
			return STATE.BANKING;
		final Area RockArea = yMine.data.getRockArea();
		if (ctx.backpack.select().count()  < 28 && !RockArea.contains(ctx.players.local().tile()))
			return STATE.WALKINGTOMINE;
		if (ctx.backpack.select().count()  < 28 && RockArea.contains(ctx.players.local().tile()))
			return STATE.MINING;
		return STATE.IDLE;
	}


	private void Bank() {
		System.out.println("Banking");
		if (ctx.bank.open()) {
			Condition.wait(new Callable<Boolean>() {
				public Boolean call() throws Exception {
					return ctx.bank.opened();
				}
			}, 500, 2);
		}
		else
		{
			ctx.camera.turnTo(ctx.bank.nearest());
		}

		if(ctx.bank.depositInventory()){
			Condition.wait(new Callable<Boolean>() {
				public Boolean call() throws Exception {
					return ctx.backpack.select().isEmpty();
				}
			}, 500, 2);
		}

		if(ctx.bank.close()){
			Condition.wait(new Callable<Boolean>() {
				public Boolean call() throws Exception {
					return !ctx.bank.opened();
				}
			}, 500, 2);
		}
	}

	private int LastMinePos = 1;

	private void Mine() {
		System.out.println("Mining");
		int[] rock = yMine.data.getRock();
		final Tile t1 = new Tile(2627,3142,0);
		final Tile t2 = new Tile(2628,3141,0);
		final Tile t3 = new Tile(2628,3140,0);
		final Tile p1 = new Tile(2627,3141,0);
		final Tile p2 = new Tile(2627,3140,0);
		final GameObject Rock1 = ctx.objects.select().at(t1).poll();
		final GameObject Rock2 = ctx.objects.select().at(t2).poll();
		final GameObject Rock3 = ctx.objects.select().at(t3).poll();
		GameObject Rock = null;
		if(Util.contains(rock, Rock1.id()))
		{
			Rock = Rock1;
		}
		else if(Util.contains(rock, Rock2.id()))
		{
			Rock = Rock2;
		}
		else if(Util.contains(rock, Rock3.id()))
		{
			Rock = Rock3;
		}
		if(Rock != null) {
			if (Rock.inViewport()
					&& ctx.players.local().animation() == -1
					&& !ctx.players.local().inMotion()) {
				if (Rock.interact("Mine")) {
					Condition.wait(new Callable<Boolean>() {
						public Boolean call() throws Exception {
							return ctx.players.local().animation() != -1;
						}
					}, 100, 20);
					if(Rock.tile().compareTo(t3) == 0)
					{
						LastMinePos = 3;
						t1.matrix(ctx).hover();
					}
					else if(Rock.tile().compareTo(t1) == 0)
					{
						LastMinePos = 1;
						t2.matrix(ctx).hover();
					}
					else if(Rock.tile().compareTo(t2) == 0)
					{
						LastMinePos = 2;
						p2.matrix(ctx).hover();
					}
				}
			} else {
				ctx.camera.angle(30);
			}
		}
		else
		{
			if(LastMinePos == 3 && ctx.players.local().tile().compareTo(p2) == 0)
			{
				if(p1.matrix(ctx).click())
				{
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.players.local().tile().compareTo(p1) == 0;
						}
					}, 500,4);
				}
				t1.matrix(ctx).hover();

			}
			else if(LastMinePos == 1 && ctx.players.local().tile().compareTo(p1) == 0)
			{
				t2.matrix(ctx).hover();
			}
			else if(LastMinePos == 2 && ctx.players.local().tile().compareTo(p1) == 0)
			{
				if(p2.matrix(ctx).click()) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ctx.players.local().tile().compareTo(p2) == 0;
						}
					}, 500, 4);
				}
				t3.matrix(ctx).hover();
			}
		}
	}

	private void WalkToMine() {
		System.out.println("Walking to Mine");
		Tile MineTile = new Tile(2627,3141, 0);
		if(MineTile.matrix(ctx).inViewport())
		{
			MineTile.matrix(ctx).click();
		}
		else {
			ctx.movement.findPath(MineTile).traverse();
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.movement.distance(ctx.players.local(), ctx.movement.destination()) < 14;
				}
			}, 250, 20);
		}
	}

	private void WalkToBank() {
		System.out.println("Walking to Bank");
		Tile BankTile = new Tile(2613,3093, 0);
		ctx.movement.findPath(BankTile).traverse();
		Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return ctx.movement.distance(ctx.players.local().tile(), ctx.movement.destination()) < 14;
			}
		}, 250, 20);
	}

	@Override
	public void messaged(MessageEvent msg) {
		if(msg.text().startsWith("You manage to ") || msg.text().startsWith("The Varrock armour")){
			oresMined++;
			System.out.println(oresMined);
		}
		if(msg.text().startsWith("You swing") && oresMined == 0)
		{
			startXP = ctx.skills.experience(Constants.SKILLS_MINING);
		}
	}

	@Override
	public void poll() {

		final STATE state = state();

		switch (state) {

		case BANKING:
			Bank();
			break;
		case WALKINGTOMINE:
			WalkToMine();
			break;
		case WALKINGTOBANK:
			WalkToBank();
			break;
		case MINING:
			Mine();
			break;
		case IDLE:
			// idle code here
			break;
		default:
			break;

		}
	}

	@Override
	public void repaint(Graphics graphics) {
		int curXp = ctx.skills.experience(Constants.SKILLS_MINING);
		int gainedXp = curXp - startXP;
		graphics.drawImage(paintBackground,0,300,null);
		Graphics2D d = (Graphics2D) graphics;
		d.setPaint(Color.WHITE);
		graphics.setFont(font);
		graphics.drawString("" + gainedXp + " (" + Util.perHour(gainedXp,startTime) + ")",230,478); //xp
		graphics.drawString("" + oresMined + " (" + Util.perHour(oresMined,startTime) + ")",230,512); //ores


	}



}
