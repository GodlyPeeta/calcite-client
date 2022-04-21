package calcite.client.mixin.extension

import net.minecraft.client.multiplayer.PlayerControllerMP
import calcite.client.mixin.client.accessor.player.AccessorPlayerControllerMP

var PlayerControllerMP.blockHitDelay: Int
    get() = (this as calcite.client.mixin.client.accessor.player.AccessorPlayerControllerMP).blockHitDelay
    set(value) {
        (this as calcite.client.mixin.client.accessor.player.AccessorPlayerControllerMP).blockHitDelay = value
    }

val PlayerControllerMP.currentPlayerItem: Int get() = (this as calcite.client.mixin.client.accessor.player.AccessorPlayerControllerMP).currentPlayerItem

fun PlayerControllerMP.syncCurrentPlayItem() = (this as calcite.client.mixin.client.accessor.player.AccessorPlayerControllerMP).kb_invokeSyncCurrentPlayItem()