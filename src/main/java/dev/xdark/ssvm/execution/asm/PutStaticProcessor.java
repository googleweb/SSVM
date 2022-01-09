package dev.xdark.ssvm.execution.asm;

import dev.xdark.ssvm.execution.ExecutionContext;
import dev.xdark.ssvm.execution.InstructionProcessor;
import dev.xdark.ssvm.execution.Result;
import dev.xdark.ssvm.mirror.InstanceJavaClass;
import org.objectweb.asm.tree.FieldInsnNode;

/**
 * Stores static field value.
 *
 * @author xDark
 */
public final class PutStaticProcessor implements InstructionProcessor<FieldInsnNode> {

	@Override
	public Result execute(FieldInsnNode insn, ExecutionContext ctx) {
		var vm = ctx.getVM();
		var owner = (InstanceJavaClass) vm.findClass(ctx.getOwner().getClassLoader(), insn.owner, true);
		var value = ctx.getStack().popGeneric();
		if (!owner.setFieldValue(insn.name, insn.desc, value)) {
			throw new IllegalStateException("No such field: " + owner.getInternalName() + '.' + insn.name + insn.desc);
		}
		return Result.CONTINUE;
	}
}
