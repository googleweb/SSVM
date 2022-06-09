package dev.xdark.ssvm.execution.asm;

import dev.xdark.ssvm.asm.VMLdcInsnNode;
import dev.xdark.ssvm.execution.ExecutionContext;
import dev.xdark.ssvm.execution.InstructionProcessor;
import dev.xdark.ssvm.execution.Result;
import dev.xdark.ssvm.execution.SafePoint;
import dev.xdark.ssvm.value.Value;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;

/**
 * Processor for {@link LdcInsnNode}.
 * <p>
 * Pushes generic ldc onto the stack.
 *
 * @author xDark
 */
public final class LdcProcessor implements InstructionProcessor<LdcInsnNode> {

	@Override
	public Result execute(LdcInsnNode insn, ExecutionContext ctx) {
		Value value;
		Object cst = insn.cst;
		if (cst instanceof Value) {
			// VM-patched.
			value = (Value) cst;
		} else {
			SafePoint safePoint = ctx.getSafePoint();
			safePoint.increment();
			try {
				value = ctx.getHelper().valueFromLdc(cst);
			} finally {
				safePoint.decrement();
			}
		}
		InsnList list = ctx.getMethod().getNode().instructions;
		list.set(insn, new VMLdcInsnNode(insn, value));
		ctx.setInsnPosition(ctx.getInsnPosition() - 1);
		return Result.CONTINUE;
	}
}
