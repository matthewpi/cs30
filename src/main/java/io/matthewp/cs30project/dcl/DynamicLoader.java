package io.matthewp.cs30project.dcl;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DynamicLoader
 *
 * ?
 */
public final class DynamicLoader {
    private static final Pattern UNICODE_PATTERN = Pattern.compile("\\\\u(\\p{XDigit}{4})");

    @Getter private DynamicSection root;
    @Getter private Map<String, DynamicSection> sections;

    @SneakyThrows(IOException.class)
    DynamicLoader(@NonNull final File file, final boolean debug) {
        this.root = new DynamicSection("");
        this.sections = new LinkedHashMap<>();

        DynamicSection currentSection = this.root;

        boolean inStringList = false;
        String stringListKey = "";
        List<String> stringList = new LinkedList<>();

        boolean inIntegerList = false;
        String integerListKey = "";
        List<Integer> integerList = new LinkedList<>();

        final BufferedReader br = new BufferedReader(new FileReader(file));

        for(String line = br.readLine(); line != null; line = br.readLine()) {
            line = line.trim();

            if(line.equals("") || line.startsWith("#") || line.startsWith("//") || line.startsWith("/*") || line.startsWith(" *") || line.startsWith("*/"))
                continue;

            /*if(line.contains(" // ")) {
                final String[] split = line.split(" // ");
                line = DynamicUtils.replaceLast(line.replaceFirst(" // ", ""), split[1], "");
            }

            if(line.contains(" # ")) {
                final String[] split = line.split(" # ");
                line = DynamicUtils.replaceLast(line.replaceFirst(" # ", ""), split[1], "");
            }*/

            if(line.contains(" {")) {
                final String prefix = (currentSection.getKey().length() == 0) ? "" : currentSection.getKey() + ".";
                final String key = prefix + line.replace(" {", "");

                currentSection = (this.getSections().get(key) == null) ? new DynamicSection(key) : this.getSections().get(key);
                this.getSections().putIfAbsent(key, currentSection);

                if(debug)
                    System.out.println("Start Section: " + currentSection.getKey());

                continue;
            }

            if(line.equals("}")) {
                String key = currentSection.getKey();

                if(debug)
                    System.out.println("End Section: " + key);

                if(key.contains(".")) {
                    final String[] split = key.split("\\.");
                    key = DynamicUtils.replaceLast(key, "." + split[split.length - 1], "");
                } else {
                    key = "";
                }

                if(key.equals(""))
                    currentSection = this.root;
                else
                    currentSection = this.getSections().get(key);

                continue;
            }

            if(line.contains(" [s")) {
                inStringList = true;
                stringListKey = line.replace(" [s", "");

                if(debug)
                    System.out.println("String List: " + stringListKey);

                continue;
            }

            if(line.equals("s]")) {
                if(inStringList) {
                    final DynamicValue value = new DynamicValue(stringList, DynamicValue.ValueType.STRING_LIST);
                    currentSection.addValue(stringListKey, value);
                }

                if(debug)
                    System.out.println("End String List: " + stringListKey);

                inStringList = false;
                stringListKey = "";
                stringList = new LinkedList<>();

                continue;
            }

            if(line.contains(" [i")) {
                inIntegerList = true;
                integerListKey = line.replace(" [i", "").replaceAll("\\s+", "");

                if(debug)
                    System.out.println("Integer List: " + integerListKey);

                continue;
            }

            if(line.equals("i]")) {
                if(inIntegerList) {
                    final DynamicValue value = new DynamicValue(integerList, DynamicValue.ValueType.INTEGER_LIST);
                    currentSection.addValue(integerListKey, value);
                }

                if(debug)
                    System.out.println("End Integer List: " + integerListKey);

                inIntegerList = false;
                integerListKey = "";
                integerList = new LinkedList<>();

                continue;
            }

            if(line.startsWith("- ")) {
                if(!inStringList && !inIntegerList)
                    continue;

                String value = DynamicUtils.replaceLast(line.split("- ")[1].replaceFirst("\"", ""), "\"", "");

                this.parseVariables(value);

                if(debug)
                    System.out.println("Value (List): " + value);

                if(inStringList)
                    stringList.add(value);

                if(inIntegerList)
                    if(DynamicUtils.isInteger(value))
                        integerList.add(Integer.valueOf(value));

                continue;
            }

            if(line.contains(": ")) {
                final String[] split = line.split(": ");

                if(split.length < 2)
                    continue;

                final DynamicValue value;
                final String key = split[0];
                final String val;

                if(split.length > 2) {
                    val = line.replaceFirst(key + ": ", "");
                } else {
                    val = split[1];
                }

                value = this.create(val);

                currentSection.addValue(key, value);

                if(debug)
                    System.out.println("Value: " + key);
            }
        }

        br.close();
    }

    private DynamicValue create(@NonNull final String input) {
        String val = this.parseVariables(input);
        final DynamicValue value;

        if(DynamicUtils.isInteger(val)) {
            value = new DynamicValue(Integer.valueOf(val), DynamicValue.ValueType.INTEGER);
        } else if(DynamicUtils.isDouble(val)) {
            value = new DynamicValue(Double.valueOf(val), DynamicValue.ValueType.DOUBLE);
        } else {
            if(val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false"))
                value = new DynamicValue(Boolean.valueOf(val), DynamicValue.ValueType.BOOLEAN);
            else
                value = new DynamicValue(DynamicUtils.replaceLast(val.replaceFirst("\"", ""), "\"", ""), DynamicValue.ValueType.STRING);
        }

        return value;
    }

    private String parseVariables(final String input) {
        String val = input;

        while(val.contains("${") && val.contains("}")) {
            final String var = val.split("\\$\\{")[1].split("}")[0];
            final DynamicValue varValue = this.getValue(var);

            if(varValue != null)
                val = val.replaceFirst("\\$\\{" + var + "}", varValue.value());
            else
                val = val.replaceFirst("\\$\\{" + var + "}", "");
        }

        final Matcher matcher = UNICODE_PATTERN.matcher(val);
        final StringBuffer buffer = new StringBuffer(val.length());

        while(matcher.find()) {
            final String ch = String.valueOf((char) Integer.parseInt(matcher.group(1), 16));
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(ch));
        }

        matcher.appendTail(buffer);

        return buffer.toString();
    }

    private DynamicValue getValue(@NonNull final String key) {
        if(key.length() == 0)
            return null;

        if(key.contains(".")) {
            final String[] split = key.split("\\.");

            if(split.length >= 2) {
                final String value = split[split.length - 1];
                final String newKey = DynamicUtils.replaceLast(key, "." + value, "");
                final DynamicSection section = this.getSections().get(newKey);

                if(section != null) {
                    final DynamicValue finalValue = section.get(value);

                    if(finalValue != null)
                        return finalValue;
                }
            }
        }

        return this.root.get(key);
    }
}
