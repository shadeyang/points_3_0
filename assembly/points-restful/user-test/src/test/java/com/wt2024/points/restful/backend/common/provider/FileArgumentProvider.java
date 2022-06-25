package com.wt2024.points.restful.backend.common.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileArgumentProvider implements ArgumentsProvider, AnnotationConsumer<FileArgumentSources> {

    private List<String> fileContentList;

    private String[] resources;
    private Charset charset;
    private String defaultContent;

    private static final String DEFAULT_SOURCE_ROOT_PATH = "data";
    private static final String DEFAULT_FILE_ENDS_WITH = ".json";

    @Override
    public void accept(FileArgumentSources fileArgumentSources) {
        this.resources = fileArgumentSources.resources();
        this.charset = getCharset(fileArgumentSources.encoding());
        this.defaultContent = fileArgumentSources.defaultContent();
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        if (this.resources.length == 0) {
            String className = context.getRequiredTestClass().getCanonicalName();
            String methodName = context.getRequiredTestMethod().getName();
            Parameter[] parameters = context.getRequiredTestMethod().getParameters();
            if (parameters.length > 0) {
                this.resources = new String[parameters.length];
                for (int index = 0; index < parameters.length; index++) {
                    FileArgumentConversion fileArgumentConversion = parameters[index].getDeclaredAnnotation(FileArgumentConversion.class);
                    if (fileArgumentConversion != null && StringUtils.isNotBlank(fileArgumentConversion.resource())) {
                        this.resources[index] = fileArgumentConversion.resource();
                    } else if (fileArgumentConversion != null && StringUtils.isNotBlank(fileArgumentConversion.alias())) {
                        this.resources[index] = String.join(File.separator,
                                DEFAULT_SOURCE_ROOT_PATH,
                                className,
                                methodName,
                                fileArgumentConversion.alias() + DEFAULT_FILE_ENDS_WITH);
                    } else {
                        this.resources[index] = String.join(File.separator,
                                DEFAULT_SOURCE_ROOT_PATH,
                                className,
                                methodName,
                                captureName(parameters[index].getType().getSimpleName()) + DEFAULT_FILE_ENDS_WITH);
                    }
                }
            }
        }

        fileContentList = Arrays.stream(this.resources)
                .map((resource) -> this.openInputStream(context, resource))
                .map(this::readStream).collect(Collectors.toList());
        Stream stream = Stream.of(Arguments.of(fileContentList.toArray(new String[0])));
        return stream;
    }

    private String captureName(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    private InputStream openInputStream(ExtensionContext context, String resource) {
        Preconditions.notBlank(resource, "Classpath resource [" + resource + "] must not be null or blank");
        return context.getRequiredTestClass().getClassLoader().getResourceAsStream(resource);
    }

    private String readStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, this.charset))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception ex) {
            stringBuilder.setLength(0);
            stringBuilder.append(this.defaultContent);
        }
        return stringBuilder.toString();
    }

    private Charset getCharset(String encoding) {
        try {
            return Charset.forName(encoding);
        } catch (Exception ex) {
            return StandardCharsets.UTF_8;
        }
    }
}
