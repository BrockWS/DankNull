package p455w0rd.danknull.init;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixableData;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraftforge.common.util.*;
import net.minecraftforge.fml.common.FMLCommonHandler;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.init.ModGlobals.NBT;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
public class ModDataFixing {

	private static final int DANKNULL_FIXES_VERSION = 101;

	public static void registerWalkers() {
		FMLCommonHandler.instance().getDataFixer().registerVanillaWalker(FixTypes.BLOCK_ENTITY, new ItemStackDataLists(TileDankNullDock.class, new String[] {
				NBT.DANKNULL_INVENTORY
		}));
	}

	public static void registerFixes() {
		final CompoundDataFixer vanillaFixer = FMLCommonHandler.instance().getDataFixer();
		final ModFixs fixs = vanillaFixer.init(ModGlobals.MODID, DANKNULL_FIXES_VERSION);
		fixs.registerFix(FixTypes.ITEM_INSTANCE, new DankNullFixer(FixTypes.ITEM_INSTANCE));
		fixs.registerFix(FixTypes.BLOCK_ENTITY, new DankNullFixer(FixTypes.BLOCK_ENTITY));
		fixs.registerFix(FixTypes.CHUNK, new DankNullFixer(FixTypes.CHUNK));
		fixs.registerFix(FixTypes.ENTITY, new DankNullFixer(FixTypes.ENTITY));
		fixs.registerFix(FixTypes.PLAYER, new DankNullFixer(FixTypes.PLAYER));
	}

	/*public static class DankNullWalker implements IDataWalker {

		@Nonnull
		@Override
		public NBTTagCompound process(@Nonnull final IDataFixer fixer, @Nonnull final NBTTagCompound nbt, final int version) {
			if (ModBlocks.DANKNULL_DOCK.getRegistryName().toString().equals(nbt.getString(NBT.ID))) {
				if (nbt.hasKey(NBT.DANKNULL_INVENTORY, Constants.NBT.TAG_COMPOUND)) {
					final NBTTagCompound itemstackHandler = nbt.getCompoundTag(NBT.DANKNULL_INVENTORY);
					DataFixesManager.processInventory(fixer, itemstackHandler, version, "Items");
					ModLogger.debug("Ran DankNullWalker on inventory {} of TileEntity {}", NBT.DANKNULL_INVENTORY, nbt.getString(NBT.ID));
				}
			}
			else if (ModItems.DANK_NULL_DOCK_ITEM.getRegistryName().toString().equals(nbt.getString(NBT.ID))) {

			}
			return nbt;
		}

	}*/

	public static class DankNullFixer implements IFixableData {

		private static final ResourceLocation oldDankNull = new ResourceLocation(ModGlobals.MODID, "dank_null");
		private static final ResourceLocation oldDankNullPanel = new ResourceLocation(ModGlobals.MODID, "dank_null_panel");

		final FixTypes fixType;

		public DankNullFixer(final FixTypes fixType) {
			this.fixType = fixType;
		}

		@Override
		public int getFixVersion() {
			return 101;
		}

		@Override
		public NBTTagCompound fixTagCompound(NBTTagCompound nbt) {
			if (fixType == FixTypes.BLOCK_ENTITY || fixType == FixTypes.CHUNK) {
				if (nbt.hasKey("Level")) {
					final NBTTagCompound lvlNBT = nbt.getCompoundTag("Level");
					if (lvlNBT.hasKey("Entities", Constants.NBT.TAG_LIST)) {
						final NBTTagList entityList = lvlNBT.getTagList("Entities", 0);
						if (!entityList.hasNoTags()) {
							System.out.println(entityList);
						}
					}
				}
				if (nbt.hasKey(NBT.DOCKEDSTACK, Constants.NBT.TAG_COMPOUND)) {
					nbt.setTag(NBT.DOCKEDSTACK, getNewDankNull(nbt.getCompoundTag(NBT.DOCKEDSTACK)));
				}
			}
			if (nbt.hasKey(NBT.ID, Constants.NBT.TAG_STRING)) {
				if (isDankNullDock(nbt)) {
					if (!DankNullUtils.getDockedDankNull(new ItemStack(nbt)).isEmpty()) {
						nbt = getNewDankDock(nbt);
					}
				}
				else if (isDankNullItem(nbt)) {
					nbt = getNewDankNull(nbt);
				}
			}
			else {
				if (nbt.hasKey("Inventory", Constants.NBT.TAG_LIST)) {
					final NBTTagList nbttaglist = nbt.getTagList("Inventory", 10);

					for (int i = 0; i < nbttaglist.tagCount(); ++i) {
						NBTTagCompound currentNBT = nbttaglist.getCompoundTagAt(i);
						if (isDankNullDock(currentNBT)) {
							if (!DankNullUtils.getDockedDankNull(new ItemStack(currentNBT)).isEmpty()) {
								currentNBT = getNewDankDock(currentNBT);
							}
						}
						else if (isDankNullItem(currentNBT)) {
							currentNBT = getNewDankNull(currentNBT);
						}
						nbttaglist.set(i, currentNBT);
					}
					nbt.setTag("Inventory", nbttaglist);
				}
				else if (nbt.hasKey(NBT.DOCKEDSTACK, Constants.NBT.TAG_COMPOUND)) {
					nbt.setTag(NBT.DOCKEDSTACK, getNewDankNull(nbt.getCompoundTag(NBT.DOCKEDSTACK)));
				}
			}
			return nbt;
		}

		private NBTTagCompound getNewDankDock(final NBTTagCompound dockNBT) {
			final ItemStack dankDock = new ItemStack(dockNBT);
			final ItemStack dankNull = DankNullUtils.getDockedDankNull(new ItemStack(dockNBT));
			if (!dankNull.isEmpty()) {
				DankNullUtils.setDockedDankNull(dankDock, new ItemStack(getNewDankNull(dankNull.serializeNBT())));
			}
			return dankDock.serializeNBT();
		}

		private NBTTagCompound getNewDankNull(final NBTTagCompound dankNullNBT) {
			final ResourceLocation regName = new ResourceLocation(dankNullNBT.getString(NBT.ID));
			//if (dankNullNBT.hasKey(NBT.DAMAGE, Constants.NBT.TAG_SHORT)) {
			final int dmg = dankNullNBT.getShort(NBT.DAMAGE);
			final String newName = regName.equals(oldDankNull) ? getNewDNRegName(dmg) : getNewPanelRegName(dmg);
			dankNullNBT.setString(NBT.ID, newName);
			dankNullNBT.setShort(NBT.DAMAGE, (short) 0);
			ModLogger.debug("Item with id {}:{} found. Updated to {}:0", regName.toString(), dmg, newName);
			//}
			return dankNullNBT;
		}

		private boolean isDankNullDock(final NBTTagCompound nbt) {
			final ResourceLocation regName = new ResourceLocation(nbt.getString(NBT.ID));
			return regName != null && regName.equals(new ResourceLocation(ModGlobals.MODID, "danknull_dock"));
		}

		private boolean isDankNullItem(final NBTTagCompound nbt) {
			final ResourceLocation regName = new ResourceLocation(nbt.getString(NBT.ID));
			return regName != null && (regName.equals(oldDankNull) || regName.equals(oldDankNullPanel));
		}

		private String getNewDNRegName(final int oldMeta) {
			return ModGlobals.MODID + ":dank_null_" + oldMeta;
		}

		private String getNewPanelRegName(final int oldMeta) {
			return ModGlobals.MODID + ":dank_null_panel_" + oldMeta;
		}

	}

}
