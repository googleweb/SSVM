package dev.xdark.ssvm.execution.asm;

import dev.xdark.ssvm.execution.ExecutionContext;
import dev.xdark.ssvm.execution.InstructionProcessor;
import dev.xdark.ssvm.execution.Result;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Loads double from local variable.
 *
 * @author xDark
 */
public final class DoubleLoadProcessor implements InstructionProcessor<VarInsnNode> {

	@Override
	public Result execute(VarInsnNode insn, ExecutionContext ctx) {
		ctx.getStack().pushWide(ctx.getLocals().load(insn.var));
		return Result.CONTINUE;
	}
}
