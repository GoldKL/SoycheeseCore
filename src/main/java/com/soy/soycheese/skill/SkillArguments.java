package com.soy.soycheese.skill;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.soy.soycheese.SoycheeseCore;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class SkillArguments {
    public final static Set<Class<?>> ACCEPT_TYPES = Set.of(Boolean.class,
            Character.class,
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            BigDecimal.class,
            BigInteger.class,
            String.class,
            SkillArguments.ObjectArguments.class,
            SkillArguments.ArrayArguments.class);
    public static <T> boolean isAcceptType(T value)
    {
        return value != null && ACCEPT_TYPES.contains(value.getClass());
    }
    final private ObjectArguments skillArguments;
    public SkillArguments(){
        skillArguments = new ObjectArguments();
    };
    public SkillArguments(SkillArguments skillArguments){
        this(skillArguments.skillArguments.object_arguments,skillArguments.skillArguments.object_types);
    };
    public SkillArguments(LinkedHashMap<String,Object> object_arguments, HashMap<String,Class<?>> object_types){
        skillArguments = new ObjectArguments(object_arguments, object_types);
    }
    public <T> void initSkillArgument(String name, T value) {
        skillArguments.initObject(name, value);
    }
    public <T> T getSkillArgument(String name, @NotNull Class<T> type) {
        return skillArguments.getObject(name, type);
    }
    public Object getSkillArgument(String name) {
        return skillArguments.getObject(name);
    }
    public void readjson(JsonObject config) {
        skillArguments.readjson(config);
    }
    public Object[] getSkillArguments() {
        return this.skillArguments.object_arguments.values().toArray();
    }
    public static class ObjectArguments{
        private final LinkedHashMap<String,Object> object_arguments = new LinkedHashMap<>();
        private final HashMap<String,Class<?>> object_types = new HashMap<>();
        private final LinkedHashMap<String,Object> object_defaults = new LinkedHashMap<>();
        ObjectArguments(){}
        ObjectArguments(LinkedHashMap<String,Object> object_arguments, HashMap<String,Class<?>> object_types){
            this.object_arguments.putAll(object_arguments);
            this.object_types.putAll(object_types);
            this.object_defaults.putAll(object_arguments);
        }
        public <T> void initObject(String name, T value) {
            if(isAcceptType(value)){
                object_arguments.put(name, value);
                object_defaults.put(name, value);
                object_types.put(name,value.getClass());
            }
            else
                throw new IllegalArgumentException("Couldn't accept argument" + name + "'s type");
        }
        public <T> T getObject(String name, @NotNull Class<T> type) {
            try {
                if (!this.object_arguments.containsKey(name)) {
                    throw new IllegalArgumentException("Object argument " + name + " not found");
                }
                Object value = this.object_arguments.get(name);
                if(!type.isInstance(value)) {
                    throw new IllegalArgumentException("Object argument " + name + " can't be cast to " + type);
                }
                return type.cast(value);
            }catch (ClassCastException e) {
                SoycheeseCore.LOGGER.error("Couldn't get Object argument {}", name, e);
                return null;
            }
        }
        public Object getObject(String name) {
            try {
                if (!this.object_arguments.containsKey(name)) {
                    throw new IllegalArgumentException("Object argument " + name + " not found");
                }
                return this.object_arguments.get(name);
            }catch (ClassCastException e) {
                SoycheeseCore.LOGGER.error("Couldn't get Object argument {}", name, e);
                return null;
            }
        }
        private void readjson(JsonObject config)
        {
            HashMap<String,Object> temp = new HashMap<>();
            for(String name : object_arguments.keySet())
            {
                try{
                    if(object_types.get(name) == Boolean.class)
                        temp.put(name,config.get(name).getAsBoolean());
                    else if (object_types.get(name) == Character.class)
                        temp.put(name,config.get(name).getAsCharacter());
                    else if (object_types.get(name) == Byte.class)
                        temp.put(name,config.get(name).getAsByte());
                    else if (object_types.get(name) == Short.class)
                        temp.put(name,config.get(name).getAsShort());
                    else if(object_types.get(name) == Integer.class)
                        temp.put(name,config.get(name).getAsInt());
                    else if(object_types.get(name) == Long.class)
                        temp.put(name,config.get(name).getAsLong());
                    else if (object_types.get(name) == Float.class)
                        temp.put(name,config.get(name).getAsFloat());
                    else if(object_types.get(name) == Double.class)
                        temp.put(name,config.get(name).getAsDouble());
                    else if (object_types.get(name) == BigDecimal.class)
                        temp.put(name,config.get(name).getAsBigDecimal());
                    else if(object_types.get(name) == BigInteger.class)
                        temp.put(name,config.get(name).getAsBigInteger());
                    else if (object_types.get(name) == String.class)
                        temp.put(name,config.get(name).getAsString());
                    else if (object_types.get(name) == SkillArguments.ObjectArguments.class)
                    {
                        SkillArguments.ObjectArguments oldObject = (SkillArguments.ObjectArguments)object_arguments.get(name);
                        SkillArguments.ObjectArguments tempObj = new SkillArguments.ObjectArguments(oldObject.object_arguments,oldObject.object_types);
                        tempObj.readjson(config.get(name).getAsJsonObject());
                        temp.put(name, tempObj);
                    }
                    else if (object_types.get(name) == SkillArguments.ArrayArguments.class)
                    {
                        SkillArguments.ArrayArguments oldObject = (SkillArguments.ArrayArguments)object_arguments.get(name);
                        SkillArguments.ArrayArguments tempArr = new SkillArguments.ArrayArguments(oldObject.array_arguments,oldObject.array_types);
                        tempArr.readjson(config.get(name).getAsJsonArray());
                        temp.put(name, tempArr);
                    }
                }
                catch (ClassCastException e) {
                    SoycheeseCore.LOGGER.error("Couldn't read skill arguments {}",name, e);
                }
            }
            for(String name : object_arguments.keySet())
            {
                Object value = temp.get(name);
                if(value != null)
                    object_arguments.put(name,value);
                else
                    object_arguments.put(name,object_defaults.get(name));
            }
        }
    }
    public static class ArrayArguments{
        private final ArrayList<Object> array_arguments = new ArrayList<>();
        private final ArrayList<Class<?>> array_types = new ArrayList<>();
        private final ArrayList<Object> array_defaults = new ArrayList<>();
        ArrayArguments(){}
        ArrayArguments(ArrayList<Object> array_arguments, ArrayList<Class<?>> array_types){
            this.array_arguments.addAll(array_arguments);
            this.array_types.addAll(array_types);
            this.array_defaults.addAll(array_arguments);
        }
        public <T> void initObject(T value) {
            if(isAcceptType(value)){
                array_arguments.add(value);
                array_defaults.add(value);
                array_types.add(value.getClass());
            }
            else
            {
                int index = array_arguments.size();
                throw new IllegalArgumentException("ArrayArguments Couldn't accept" + index + "argument's type");
            }
        }
        public <T> T getObject(int index, @NotNull Class<T> type) {
            try {
                if (index >= array_arguments.size() || index < 0) {
                    throw new IllegalArgumentException("Array argument " + index + " not found");
                }
                Object value = this.array_arguments.get(index);
                if(!type.isInstance(value)) {
                    throw new IllegalArgumentException("Array argument " + index + " can't be cast to " + type);
                }
                return type.cast(value);
            }catch (ClassCastException e) {
                SoycheeseCore.LOGGER.error("Couldn't get Array argument {}", index, e);
                return null;
            }
        }
        public Object getObject(int index) {
            try {
                if (index >= array_arguments.size() || index < 0) {
                    throw new IllegalArgumentException("Array argument " + index + " not found");
                }
                return this.array_arguments.get(index);
            }catch (ClassCastException e) {
                SoycheeseCore.LOGGER.error("Couldn't get Array argument {}", index, e);
                return null;
            }
        }
        private void readjson(JsonArray config)
        {
            ArrayList<Object> temp = new ArrayList<>();
            for(int i = 0;i < array_arguments.size();i++)
            {
                try{
                    if(array_types.get(i) == Boolean.class)
                        temp.add(config.get(i).getAsBoolean());
                    else if (array_types.get(i) == Character.class)
                        temp.add(config.get(i).getAsCharacter());
                    else if (array_types.get(i) == Byte.class)
                        temp.add(config.get(i).getAsByte());
                    else if (array_types.get(i) == Short.class)
                        temp.add(config.get(i).getAsShort());
                    else if(array_types.get(i) == Integer.class)
                        temp.add(config.get(i).getAsInt());
                    else if(array_types.get(i) == Long.class)
                        temp.add(config.get(i).getAsLong());
                    else if (array_types.get(i) == Float.class)
                        temp.add(config.get(i).getAsFloat());
                    else if(array_types.get(i) == Double.class)
                        temp.add(config.get(i).getAsDouble());
                    else if (array_types.get(i) == BigDecimal.class)
                        temp.add(config.get(i).getAsBigDecimal());
                    else if(array_types.get(i) == BigInteger.class)
                        temp.add(config.get(i).getAsBigInteger());
                    else if (array_types.get(i) == String.class)
                        temp.add(config.get(i).getAsString());
                    else if (array_types.get(i) == SkillArguments.ObjectArguments.class)
                    {
                        SkillArguments.ObjectArguments oldObject = (SkillArguments.ObjectArguments)array_arguments.get(i);
                        SkillArguments.ObjectArguments tempObj = new SkillArguments.ObjectArguments(oldObject.object_arguments,oldObject.object_types);
                        tempObj.readjson(config.get(i).getAsJsonObject());
                        temp.add(tempObj);
                    }
                    else if (array_types.get(i) == SkillArguments.ArrayArguments.class)
                    {
                        SkillArguments.ArrayArguments oldObject = (SkillArguments.ArrayArguments)array_arguments.get(i);
                        SkillArguments.ArrayArguments tempArr = new SkillArguments.ArrayArguments(oldObject.array_arguments,oldObject.array_types);
                        tempArr.readjson(config.get(i).getAsJsonArray());
                        temp.add(tempArr);
                    }
                }
                catch (ClassCastException e) {
                    SoycheeseCore.LOGGER.error("Couldn't read skill arguments {}",i, e);
                }
            }
            for(int i = 0;i < array_arguments.size();i++)
            {
                Object value = temp.get(i);
                if(value != null)
                    array_arguments.set(i,value);
                else
                    array_arguments.set(i,array_defaults.get(i));
            }
        }
    }
}
