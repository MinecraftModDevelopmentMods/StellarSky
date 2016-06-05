package stellarium.lib.hierarchy.internal;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import com.sun.xml.internal.ws.org.objectweb.asm.Type;

import net.minecraft.launchwrapper.IClassTransformer;
import stellarium.lib.hierarchy.Hierarchy;
import stellarium.lib.hierarchy.HierarchyWrapper;

public class HierarchyClassTransformer implements IClassTransformer {
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		ClassReader reader = new ClassReader(basicClass);
		ClassNode node = new ClassNode();
		reader.accept(node, 0);

		if(node.visibleAnnotations == null)
			return basicClass;

		for(AnnotationNode annotation : node.visibleAnnotations) {
			if(annotation.desc.equals(Type.getDescriptor(Hierarchy.class))) {
				ClassWriter writer = new ClassWriter(0);
				reader.accept(writer, 0);
				writer.visitField(Opcodes.ACC_PUBLIC,
						HierarchyWrapper.WRAPPER_REF_FIELD,
						Type.getDescriptor(Object.class),
						null, null).visitEnd();
				return writer.toByteArray();
			}
		}

		return basicClass;
	}
}