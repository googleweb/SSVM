package dev.xdark.ssvm.natives;

import dev.xdark.ssvm.VirtualMachine;
import dev.xdark.ssvm.execution.Result;
import dev.xdark.ssvm.mirror.InstanceJavaClass;
import dev.xdark.ssvm.value.*;
import lombok.experimental.UtilityClass;
import lombok.val;

/**
 * VM intrinsics.
 *
 * @author xDark
 */
@UtilityClass
public class IntrinsicsNatives {

	/**
	 * @param vm
	 * 		VM instance.
	 */
	public void init(VirtualMachine vm) {
		mathIntrinsics(vm);
		objectIntrinsics(vm);
		stringIntrinsics(vm);
		characterIntrinsics(vm);
	}

	private void mathIntrinsics(VirtualMachine vm) {
		val vmi = vm.getInterface();
		val jc = (InstanceJavaClass) vm.findBootstrapClass("java/lang/Math");
		vmi.setInvoker(jc, "min", "(II)I", ctx -> {
			printIntrinsic("min_II");
			val locals = ctx.getLocals();
			ctx.setResult(new IntValue(Math.min(locals.load(0).asInt(), locals.load(1).asInt())));
			return Result.ABORT;
		});
		vmi.setInvoker(jc, "min", "(JJ)J", ctx -> {
			printIntrinsic("min_JJ");
			val locals = ctx.getLocals();
			ctx.setResult(new LongValue(Math.min(locals.load(0).asLong(), locals.load(2).asLong())));
			return Result.ABORT;
		});
		vmi.setInvoker(jc, "min", "(FF)F", ctx -> {
			printIntrinsic("min_FF");
			val locals = ctx.getLocals();
			ctx.setResult(new FloatValue(Math.min(locals.load(0).asFloat(), locals.load(1).asFloat())));
			return Result.ABORT;
		});
		vmi.setInvoker(jc, "min", "(DD)D", ctx -> {
			printIntrinsic("min_DD");
			val locals = ctx.getLocals();
			ctx.setResult(new DoubleValue(Math.min(locals.load(0).asDouble(), locals.load(2).asDouble())));
			return Result.ABORT;
		});
		vmi.setInvoker(jc, "max", "(II)I", ctx -> {
			printIntrinsic("max_II");
			val locals = ctx.getLocals();
			ctx.setResult(new IntValue(Math.max(locals.load(0).asInt(), locals.load(1).asInt())));
			return Result.ABORT;
		});
		vmi.setInvoker(jc, "max", "(JJ)J", ctx -> {
			printIntrinsic("max_JJ");
			val locals = ctx.getLocals();
			ctx.setResult(new LongValue(Math.max(locals.load(0).asLong(), locals.load(2).asLong())));
			return Result.ABORT;
		});
		vmi.setInvoker(jc, "max", "(FF)F", ctx -> {
			printIntrinsic("max_FF");
			val locals = ctx.getLocals();
			ctx.setResult(new FloatValue(Math.max(locals.load(0).asFloat(), locals.load(1).asFloat())));
			return Result.ABORT;
		});
		vmi.setInvoker(jc, "max", "(DD)D", ctx -> {
			printIntrinsic("max_DD");
			val locals = ctx.getLocals();
			ctx.setResult(new DoubleValue(Math.max(locals.load(0).asDouble(), locals.load(2).asDouble())));
			return Result.ABORT;
		});
		vmi.setInvoker(jc, "abs", "(I)I", ctx -> {
			printIntrinsic("abs_I");
			val locals = ctx.getLocals();
			ctx.setResult(new IntValue(Math.abs(locals.load(0).asInt())));
			return Result.ABORT;
		});
		vmi.setInvoker(jc, "abs", "(J)J", ctx -> {
			val locals = ctx.getLocals();
			ctx.setResult(new LongValue(Math.abs(locals.load(0).asLong())));
			return Result.ABORT;
		});
		vmi.setInvoker(jc, "abs", "(F)F", ctx -> {
			printIntrinsic("abs_F");
			val locals = ctx.getLocals();
			ctx.setResult(new FloatValue(Math.abs(locals.load(0).asFloat())));
			return Result.ABORT;
		});
		vmi.setInvoker(jc, "abs", "(D)D", ctx -> {
			printIntrinsic("abs_D");
			val locals = ctx.getLocals();
			ctx.setResult(new DoubleValue(Math.abs(locals.load(0).asDouble())));
			return Result.ABORT;
		});
	}

	private static void objectIntrinsics(VirtualMachine vm) {
		val vmi = vm.getInterface();
		val jc = vm.getSymbols().java_lang_Object;
		vmi.setInvoker(jc, "equals", "(Ljava/lang/Object;)Z", ctx -> {
			val locals = ctx.getLocals();
			ctx.setResult(locals.load(0) == locals.load(1) ? IntValue.ONE : IntValue.ZERO);
			return Result.ABORT;
		});
	}

	private static void stringIntrinsics(VirtualMachine vm) {
		val vmi = vm.getInterface();
		val jc = vm.getSymbols().java_lang_String;
		// This will only work on JDK 8, sadly.
		if (jc.hasVirtualField("value", "[C")) {
			vmi.setInvoker(jc, "length", "()I", ctx -> {
				ctx.setResult(new IntValue(((ArrayValue)ctx.getLocals().<InstanceValue>load(0).getValue("value", "[C")).getLength()));
				return Result.ABORT;
			});
			vmi.setInvoker(jc, "hashCode", "()I", ctx -> {
				printIntrinsic("String_hashCode");
				val _this = ctx.getLocals().<InstanceValue>load(0);
				int hc = _this.getInt("hash");
				if (hc == 0) {
					val value = (ArrayValue) _this.getValue("value", "[C");
					for (int i = 0, j = value.getLength(); i < j; i++) {
						hc = 31 * hc + value.getChar(i);
					}
					_this.setInt("hash", hc);
				}
				ctx.setResult(new IntValue(hc));
				return Result.ABORT;
			});
			vmi.setInvoker(jc, "lastIndexOf", "(II)I", ctx -> {
				val locals = ctx.getLocals();
				val _this = locals.<InstanceValue>load(0);
				val chars = (ArrayValue) _this.getValue("value", "[C");
				int ch = locals.load(1).asInt();
				int fromIndex = locals.load(2).asInt();
				ctx.setResult(new IntValue(lastIndexOf(chars, ch, fromIndex)));
				return Result.ABORT;
			});
			vmi.setInvoker(jc, "indexOf", "([CII[CIII)I", ctx -> {
				val locals = ctx.getLocals();
				val source = locals.<ArrayValue>load(0);
				int sourceOffset = locals.load(1).asInt();
				int sourceCount = locals.load(2).asInt();
				val target = locals.<ArrayValue>load(3);
				int targetOffset = locals.load(4).asInt();
				int targetCount = locals.load(5).asInt();
				int fromIndex = locals.load(6).asInt();
				ctx.setResult(new IntValue(indexOf(source, sourceOffset, sourceCount, target, targetOffset, targetCount, fromIndex)));
				return Result.ABORT;
			});
			vmi.setInvoker(jc, "indexOf", "(II)I", ctx -> {
				val locals = ctx.getLocals();
				val _this = locals.<InstanceValue>load(0);
				val chars = (ArrayValue) _this.getValue("value", "[C");
				int ch = locals.load(1).asInt();
				int fromIndex = locals.load(2).asInt();
				ctx.setResult(new IntValue(indexOf(chars, ch, fromIndex)));
				return Result.ABORT;
			});
			vmi.setInvoker(jc, "equals", "(Ljava/lang/Object;)Z", ctx -> {
				ret:
				{
					val locals = ctx.getLocals();
					val other = locals.<ObjectValue>load(1);
					if (other.getJavaClass() != jc) {
						ctx.setResult(IntValue.ZERO);
					} else {
						val _this = locals.<InstanceValue>load(0);
						val chars = (ArrayValue) _this.getValue("value", "[C");
						val chars2 = (ArrayValue) ((InstanceValue)other).getValue("value", "[C");
						int len = chars.getLength();
						if (len != chars2.getLength()) {
							ctx.setResult(IntValue.ZERO);
						} else {
							while (len-- != 0) {
								if (chars.getChar(len) != chars2.getChar(len)) {
									ctx.setResult(IntValue.ZERO);
									break ret;
								}
							}
							ctx.setResult(IntValue.ONE);
						}
					}
				}
				return Result.ABORT;
			});
		}
	}

	private int lastIndexOf(ArrayValue value, int ch, int fromIndex) {
		if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
			int i = Math.min(fromIndex, value.getLength() - 1);
			for (; i >= 0; i--) {
				if (value.getChar(i) == ch) {
					return i;
				}
			}
			return -1;
		} else {
			return lastIndexOfSupplementary(value, ch, fromIndex);
		}
	}

	private int lastIndexOfSupplementary(ArrayValue value, int ch, int fromIndex) {
		if (Character.isValidCodePoint(ch)) {
			char hi = Character.highSurrogate(ch);
			char lo = Character.lowSurrogate(ch);
			int i = Math.min(fromIndex, value.getLength() - 2);
			for (; i >= 0; i--) {
				if (value.getChar(i) == hi && value.getChar(i + 1) == lo) {
					return i;
				}
			}
		}
		return -1;
	}

	private int indexOf(ArrayValue source, int sourceOffset, int sourceCount,
						ArrayValue target, int targetOffset, int targetCount,
						int fromIndex) {
		if (fromIndex >= sourceCount) {
			return (targetCount == 0 ? sourceCount : -1);
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (targetCount == 0) {
			return fromIndex;
		}

		char first = target.getChar(targetOffset);
		int max = sourceOffset + (sourceCount - targetCount);

		for (int i = sourceOffset + fromIndex; i <= max; i++) {
			if (source.getChar(i) != first) {
				while (++i <= max && source.getChar(i) != first) ;
			}

			if (i <= max) {
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = targetOffset + 1; j < end && source.getChar(j)
						== target.getChar(k); j++, k++)
					;

				if (j == end) {
					return i - sourceOffset;
				}
			}
		}
		return -1;
	}

	private int indexOf(ArrayValue value, int ch, int fromIndex) {
		int max = value.getLength();
		if (fromIndex < 0) {
			fromIndex = 0;
		} else if (fromIndex >= max) {
			return -1;
		}

		if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
			for (int i = fromIndex; i < max; i++) {
				if (value.getChar(i) == ch) {
					return i;
				}
			}
			return -1;
		} else {
			return indexOfSupplementary(value, ch, fromIndex);
		}
	}

	private int indexOfSupplementary(ArrayValue value, int ch, int fromIndex) {
		if (Character.isValidCodePoint(ch)) {
			char hi = Character.highSurrogate(ch);
			char lo = Character.lowSurrogate(ch);
			int max = value.getLength() - 1;
			for (int i = fromIndex; i < max; i++) {
				if (value.getChar(i) == hi && value.getChar(i + 1) == lo) {
					return i;
				}
			}
		}
		return -1;
	}

	private void characterIntrinsics(VirtualMachine vm) {
		val vmi = vm.getInterface();
		val jc = (InstanceJavaClass) vm.findBootstrapClass("java/lang/Character");
		vmi.setInvoker(jc, "toLowerCase", "(I)I", ctx -> {
			ctx.setResult(new IntValue(Character.toLowerCase(ctx.getLocals().load(0).asInt())));
			return Result.ABORT;
		});
		vmi.setInvoker(jc, "toUpperCase", "(I)I", ctx -> {
			ctx.setResult(new IntValue(Character.toUpperCase(ctx.getLocals().load(0).asInt())));
			return Result.ABORT;
		});
	}

	private void printIntrinsic(String name) {
		//System.err.println("[TRACE] calling intrinsic: " + name);
	}
}