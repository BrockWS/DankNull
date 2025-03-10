package p455w0rd.danknull.integration.waila;

import java.util.List;

import mcp.mobius.waila.api.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.integration.WAILA;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.TextUtils;

/**
 * @author p455w0rd
 *
 */
public class WAILADankNullDockProvider implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
		final ItemStack stack = new ItemStack(ModBlocks.DANKNULL_DOCK);
		final TileEntity tile = accessor.getTileEntity();
		if (tile != null && tile instanceof TileDankNullDock) {
			final TileDankNullDock te = (TileDankNullDock) tile;
			final NBTTagCompound nbttagcompound = new NBTTagCompound();
			te.writeToNBT(nbttagcompound);
			stack.setTagInfo("BlockEntityTag", nbttagcompound);
		}
		return stack;
	}

	@Override
	public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
		final TileDankNullDock dankDock = (TileDankNullDock) accessor.getTileEntity();
		//final String dankNull = "/d" + (Options.callItDevNull ? "ev" : "ank") + "/null";
		//final String msg = TextUtils.translate("dn.right_click_with.desc") + (dankDock.getDankNull().isEmpty() ? " " + dankNull : " " + TextUtils.translate("dn.empty_hand_open.desc"));
		//final InventoryDankNull dankDockInventory = dankDock.getInventory();
		if (!dankDock.getDankNull().isEmpty()) {
			final ItemStack dockedDankNull = dankDock.getDankNull();
			if (!dockedDankNull.isEmpty()) {
				currenttip.add(WAILA.toolTipEnclose);
				currenttip.add(ModGlobals.Rarities.getRarityFromMeta(DankNullUtils.getMeta(dockedDankNull)).getColor() + "" + dockedDankNull.getDisplayName() + "" + TextFormatting.GRAY + " Docked");
				final ItemStack selectedStack = DankNullUtils.getSelectedStack(dockedDankNull);
				if (!selectedStack.isEmpty()) {
					currenttip.add(selectedStack.getDisplayName() + " " + TextUtils.translate("dn.selected.desc"));
					currenttip.add(TextUtils.translate("dn.count.desc") + ": " + (DankNullUtils.isCreativeDankNull(dockedDankNull) ? TextUtils.translate("dn.infinite.desc") : selectedStack.getCount()));
					currenttip.add(TextUtils.translate("dn.extract_mode.desc") + ": " + DankNullUtils.getExtractionModeForStack(dockedDankNull, selectedStack).getTooltip());
				}
				currenttip.add(WAILA.toolTipEnclose);
			}
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
		return null;
	}

	@Override
	public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te, final NBTTagCompound tag, final World world, final BlockPos pos) {
		return null;
	}
}