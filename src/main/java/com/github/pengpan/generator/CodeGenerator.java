package com.github.pengpan.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Paths;

public class CodeGenerator {

    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://192.168.50.54:3306/captcha_db", "root", "root")
                .globalConfig(builder -> builder
                        .author("pengpan")
                        .outputDir(Paths.get(System.getProperty("user.dir")) + "/src/main/java")
                        .commentDate("yyyy-MM-dd HH:mm:ss")
                )
                .packageConfig(builder -> builder
                        .parent("com.github.pengpan")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .xml("mapper.xml")
                )
                .strategyConfig(builder -> builder
                        .entityBuilder()
                        .enableLombok()
                )
                .strategyConfig(builder ->
                        builder.addTablePrefix("t_")
                )
                .strategyConfig(builder -> builder
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
