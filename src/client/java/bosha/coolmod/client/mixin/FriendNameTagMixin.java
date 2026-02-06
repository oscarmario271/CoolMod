package bosha.coolmod.client.mixin;

import bosha.coolmod.client.FriendManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityRenderer.class)
public class FriendNameTagMixin {

    @ModifyArg(
            method = "renderLabelIfPresent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabel(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
            ),
            index = 1
    )
    private Text modifyNameTag(Text original, Object entity) {
        if (entity instanceof PlayerEntity player) {
            if (FriendManager.isFriend(player.getUuid())) {
                return Text.literal("[Friend] ").append(original);
            }
        }
        return original;
    }
}
