package com.xiaoyv.gradle.crawler

import com.xiaoyv.crawler.annotation.CrawlerMethod
import com.xiaoyv.crawler.annotation.CrawlerObj
import com.xiaoyv.crawler.common.entity.CrawlerFunction
import com.xiaoyv.crawler.common.entity.CrawlerInfo
import com.xiaoyv.crawler.common.entity.CrawlerManifest
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * FindClassVisitor
 *
 * @author why
 * @since 11/14/23
 */
class CrawlerClassVisitor(private val crawlerManifest: CrawlerManifest) :
    ClassVisitor(Opcodes.ASM9) {

    private var crawlerInfo: CrawlerInfo? = null
    private var className = ""

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        className = name.orEmpty().replace('/', '.')
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitAnnotation(desc: String?, visible: Boolean): AnnotationVisitor? {
        val visitor = super.visitAnnotation(desc, visible)
        if (desc == Type.getDescriptor(CrawlerObj::class.java)) {
            crawlerInfo = CrawlerInfo(className = className)
            return object : AnnotationVisitor(api, visitor) {
                override fun visit(name: String?, value: Any?) {
                    super.visit(name, value)
                    when (name) {
                        CrawlerObj::name.name -> {
                            crawlerInfo?.name = value.toString()
                        }

                        CrawlerObj::description.name -> {
                            crawlerInfo?.description = value.toString()
                        }

                        CrawlerObj::version.name -> {
                            crawlerInfo?.version = value.toString().toIntOrNull() ?: 0
                        }
                    }
                }
            }
        }
        return visitor
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val argumentTypes = Type.getArgumentTypes(descriptor)
        var function: CrawlerFunction? = null
        return object : MethodVisitor(
            api,
            super.visitMethod(access, name, descriptor, signature, exceptions)
        ) {

            override fun visitAnnotation(
                descriptor: String?,
                visible: Boolean
            ): AnnotationVisitor {
                val visitor = super.visitAnnotation(descriptor, visible)
                if (descriptor == Type.getDescriptor(CrawlerMethod::class.java)) {
                    function = CrawlerFunction(name = name.orEmpty()).apply {
                        paramType.addAll(argumentTypes.map { it.className })
                        crawlerInfo?.function?.add(this)
                    }
                }

                return object : AnnotationVisitor(api, visitor) {
                    override fun visit(name: String?, value: Any?) {
                        super.visit(name, value)
                        when (name) {
                            CrawlerMethod::description.name -> {
                                function?.description = value.toString()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        crawlerInfo?.let { crawlerManifest.crawlers.add(it) }
    }
}