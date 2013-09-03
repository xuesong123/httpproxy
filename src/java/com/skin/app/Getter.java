/*
 * $RCSfile: Getter.java,v $$
 * $Revision: 1.1  $
 * $Date: 2007-4-16  $
 *
 * Copyright (C) 2008 Skin, Inc. All rights reserved.
 *
 * This software is the proprietary information of Skin, Inc.
 * Use is subject to license terms.
 */
package com.skin.app;

/**
 * <p>Title: Getter</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author xuesong.net
 * @version 1.0
 */
public abstract class Getter
{
    /**
     * @return String
     */
    public abstract String getValue();

    /**
     * @param name
     * @return String
     */
    public abstract String getValue(String name);

    /**
     * @param name
     * @return String
     */
    public String getValue(String name, String defaultValue)
    {
        String s = getValue(name);

        return (s != null ? s : defaultValue);
    }

    /**
     * @return String
     */
    public String getString()
    {
        return this.getValue();
    }

    /**
     * @param name
     * @return String
     */
    public String getString(String name)
    {
        return this.getValue(name);
    }

    /**
     * @param name
     * @param defaultValue
     * @return String
     */
    public String getString(String name, String defaultValue)
    {
        return this.getValue(name, defaultValue);
    }

    /**
     * @param applicationName
     * @param moduleName
     * @param name
     * @return Character
     */
    public Character getCharacter(String name)
    {
        return getCharacter(name, null);
    }

    /**
     * @param applicationName
     * @param moduleName
     * @param name
     * @param defaultValue
     * @return Character
     */
    public Character getCharacter(String name, Character defaultValue)
    {
        String s = getString(name);

        return parseCharacter(s, defaultValue);
    }

    /**
     * @param name
     * @return Boolean
     */
    public Boolean getBoolean(String name)
    {
        return getBoolean(name, null);
    }

    /**
     * @param name
     * @param defaultValue
     * @return Boolean
     */
    public Boolean getBoolean(String name, Boolean defaultValue)
    {
        String s = getString(name);

        return parseBoolean(s, defaultValue);
    }

    /**
     * @param name
     * @return Byte
     */
    public Byte getByte(String name)
    {
        return getByte(name, null);
    }

    /**
     * @param name
     * @param defaultValue
     * @return Byte
     */
    public Byte getByte(String name, Byte defaultValue)
    {
        String s = getString(name);

        return parseByte(s, defaultValue);
    }

    /**
     * @param name
     * @return Short
     */
    public Short getShort(String name)
    {
        return getShort(name, null);
    }

    /**
     * @param name
     * @param defaultValue
     * @return Short
     */
    public Short getShort(String name, Short defaultValue)
    {
        String s = getString(name);

        return parseShort(s, defaultValue);
    }

    /**
     * @param name
     * @return Integer
     */
    public Integer getInteger(String name)
    {
        return getInteger(name, null);
    }

    /**
     * @param name
     * @param defaultValue
     * @return Integer
     */
    public Integer getInteger(String name, Integer defaultValue)
    {
        String s = getString(name);

        return parseInt(s, defaultValue);
    }

    /**
     * @param name
     * @return Float
     */
    public Float getFloat(String name)
    {
        return getFloat(name, null);
    }

    /**
     * @param name
     * @param defaultValue
     * @return Float
     */
    public Float getFloat(String name, Float defaultValue)
    {
        String s = getString(name);

        return parseFloat(s, defaultValue);
    }

    /**
     * @param name
     * @return Double
     */
    public Double getDouble(String name)
    {
        return getDouble(name, null);
    }

    /**
     * @param name
     * @param defaultValue
     * @return Double
     */
    public Double getDouble(String name, Double defaultValue)
    {
        String s = getString(name);

        return parseDouble(s, defaultValue);
    }

    /**
     * @param name
     * @return Long
     */
    public Long getLong(String name)
    {
        return getLong(name, null);
    }

    /**
     * @param name
     * @param defaultValue
     * @return Long
     */
    public Long getLong(String name, Long defaultValue)
    {
        String s = getString(name);

        return parseLong(s, defaultValue);
    }

    /**
     * @param name
     * @param format
     * @return java.util.Date
     */
    public java.util.Date getDate(String name, String format)
    {
        String s = getString(name);

        java.util.Date date = parseDate(s, format);

        return date;
    }

    /**
     * @param name
     * @param format
     * @return java.sql.Date
     */
    public java.sql.Date getSqlDate(String name, String format)
    {
        java.util.Date date = getDate(name, format);

        java.sql.Date sqlDate = null;

        if(date != null)
        {
            sqlDate = new java.sql.Date(date.getTime());
        }

        return sqlDate;
    }

    /**
     * @param name
     * @param format
     * @return java.sql.Timestamp
     */
    public java.sql.Timestamp getTimestamp(String name, String format)
    {
        java.util.Date date = getDate(name, format);

        java.sql.Timestamp timestamp = null;

        if(date != null)
        {
            timestamp = new java.sql.Timestamp(date.getTime());
        }

        return timestamp;
    }

    /**
     * @param s
     * @return Character
     */
    public Character parseCharacter(String s)
    {
        return parseCharacter(s, null);
    }

    /**
     * @param s
     * @return Boolean
     */
    public Boolean parseBoolean(String s)
    {
        return parseBoolean(s, null);
    }

    /**
     * @param s
     * @return Byte
     */
    public Byte parseByte(String s)
    {
        return parseByte(s, null);
    }

    /**
     * @param s
     * @return Short
     */
    public Short parseShort(String s)
    {
        return parseShort(s, null);
    }

    /**
     * @param s
     * @return Integer
     */
    public Integer parseInt(String s)
    {
        return parseInt(s, null);
    }

    /**
     * @param s
     * @return Float
     */
    public Float parseFloat(String s)
    {
        return parseFloat(s, null);
    }

    /**
     * @param s
     * @return Double
     */
    public Double parseDouble(String s)
    {
        return parseDouble(s, null);
    }

    /**
     * @param s
     * @return Long
     */
    public Long parseLong(String s)
    {
        return parseLong(s, null);
    }

    /**
     * @param s
     * @param defaultValue
     * @return Character
     */
    public Character parseCharacter(String s, Character defaultValue)
    {
        Character result = defaultValue;

        if(s != null && s.trim().length() > 0)
        {
            try
            {
                char value = Character.valueOf(s.trim().charAt(0));

                result = new Character(value);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return result;
    }

    /**
     * @param s
     * @param defaultValue
     * @return Boolean
     */
    public Boolean parseBoolean(String s, Boolean defaultValue)
    {
        Boolean result = defaultValue;

        if(s != null)
        {
            try
            {
                String b = s.toLowerCase();

                boolean value = ("1".equals(b) || "y".equals(b) || "on".equals(b) || "yes".equals(b) || "true".equals(b));

                result = new Boolean(value);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return result;
    }

    /**
     * @param s
     * @param defaultValue
     * @return Byte
     */
    public Byte parseByte(String s, Byte defaultValue)
    {
        Byte result = defaultValue;

        if(s != null)
        {
            try
            {
                byte value = Byte.parseByte(s);

                result = new Byte(value);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return result;
    }

    /**
     * @param s
     * @param defaultValue
     * @return parseShort
     */
    public Short parseShort(String s, Short defaultValue)
    {
        Short result = defaultValue;

        if(s != null)
        {
            try
            {
                short value = Short.parseShort(s);

                result = new Short(value);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return result;
    }

    /**
     * @param s
     * @param defaultValue
     * @return Integer
     */
    public Integer parseInt(String s, Integer defaultValue)
    {
        Integer result = defaultValue;

        if(s != null)
        {
            try
            {
                int value = Integer.parseInt(s);

                result = new Integer(value);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return result;
    }

    /**
     * @param s
     * @param defaultValue
     * @return Float
     */
    public Float parseFloat(String s, Float defaultValue)
    {
        Float result = defaultValue;

        if(s != null)
        {
            try
            {
                float value = Float.parseFloat(s);

                result = new Float(value);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return result;
    }

    /**
     * @param s
     * @param defaultValue
     * @return Double
     */
    public Double parseDouble(String s, Double defaultValue)
    {
        Double result = defaultValue;

        if(s != null)
        {
            try
            {
                double value = Double.parseDouble(s);

                result = new Double(value);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return result;
    }

    /**
     * @param s
     * @param defaultValue
     * @return Long
     */
    public Long parseLong(String s, Long defaultValue)
    {
        Long result = defaultValue;

        if(s != null)
        {
            try
            {
                long value = Long.parseLong(s);

                result = new Long(value);
            }
            catch(NumberFormatException e)
            {
            }
        }

        return result;
    }

    /**
     * @param s
     * @param format
     * @return java.util.Date
     */
    public java.util.Date parseDate(String s, String format)
    {
        java.util.Date date = null;

        if(s != null)
        {
            try
            {
                java.text.DateFormat df = new java.text.SimpleDateFormat(format);

                date = df.parse(s);
            }
            catch(java.text.ParseException e)
            {
            }
        }

        return date;
    }

    /**
     * @param <T>
     * @param model
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(Class<T> model, String name)
    {
        Object value = null;

        String className = model.getName();

        if(className.equals("char") || className.equals("java.lang.Character"))
        {
            value = getCharacter(name);
        }
        else if(className.equals("boolean") || className.equals("java.lang.Boolean"))
        {
            value = getBoolean(name);
        }
        else if(className.equals("byte") || className.equals("java.lang.Byte"))
        {
            value = getByte(name);
        }
        else if(className.equals("short") || className.equals("java.lang.Short"))
        {
            value = getShort(name);
        }
        else if(className.equals("int") || className.equals("java.lang.Integer"))
        {
            value = getInteger(name);
        }
        else if(className.equals("float") || className.equals("java.lang.Float"))
        {
            value = getFloat(name);
        }
        else if(className.equals("double") || className.equals("java.lang.Double"))
        {
            value = getDouble(name);
        }
        else if(className.equals("long") || className.equals("java.lang.Long"))
        {
            value = getLong(name);
        }
        else if(className.equals("java.lang.String"))
        {
            value = getString(name);
        }
        else if(className.equals("java.util.Date"))
        {
            value = getDate(name, "yyyy-MM-dd hh:mm:ss");
        }
        else if(className.equals("java.sql.Date"))
        {
            value = getSqlDate(name, "yyyy-MM-dd hh:mm:ss");
        }
        else if(className.equals("java.sql.Timestamp"))
        {
            value = getTimestamp(name, "yyyy-MM-dd hh:mm:ss");
        }

        return (T)value;
    }
}
