/*
 * Distributed as part of c3p0 v.0.9.0-pre2
 *
 * Copyright (C) 2005 Machinery For Change, Inc.
 *
 * Author: Steve Waldman <swaldman@mchange.com>
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2.1, as 
 * published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; see the file LICENSE.  If not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */


package com.mchange.v2.log;

import com.mchange.v1.util.StringTokenizerUtils;
import com.mchange.v2.cfg.MultiPropertiesConfig;

public abstract class MLog
{
    final static NameTransformer transformer;
    final static MLog mlog;

    final static MultiPropertiesConfig CONFIG;

    static
    {
	String[] defaults = new String[]
	{
	    "/com/mchange/v2/log/default-mchange-log.properties",
	    "/mchange-log.properties",
	    "/"
	};
	CONFIG = MultiPropertiesConfig.readVmConfig( defaults, null );

	String classnamesStr = CONFIG.getProperty("com.mchange.v2.log.MLog");
	String[] classnames = null;
	if (classnamesStr == null)
	    classnamesStr = CONFIG.getProperty("com.mchange.v2.log.mlog");
	if (classnamesStr != null)
	    classnames = StringTokenizerUtils.tokenizeToArray( classnamesStr, ", \t\r\n" );

	boolean warn = false;
	MLog tmpml = null;
	if (classnames != null)
	    tmpml = findByClassnames( classnames );
	if (tmpml == null)
	    tmpml = findByClassnames( MLogClasses.CLASSNAMES );
	if (tmpml == null)
	    {
		warn = true;
		tmpml = new FallbackMLog();
	    }
	mlog = tmpml;
	if (warn)
	    info("Using " + mlog.getClass().getName() + " -- Named logger's not supported, everything goes to System.err.");

	NameTransformer tmpt = null;
	String tClassName = CONFIG.getProperty("com.mchange.v2.log.NameTransformer");
	if (tClassName == null)
	    tClassName = CONFIG.getProperty("com.mchange.v2.log.nametransformer");
	try
	    { 
		if (tClassName != null)
		    tmpt = (NameTransformer) Class.forName( tClassName ).newInstance();
	    }
	catch ( Exception e )
	    {
		System.err.println("Failed to instantiate com.mchange.v2.log.NameTransformer '" + tClassName + "'!"); 
		e.printStackTrace();
	    }
	transformer = tmpt;
    }

    public static MLog findByClassnames( String[] classnames )
    {
	for (int i = 0, len = classnames.length; i < len; ++i)
	    {
		try { return (MLog) Class.forName( classnames[i] ).newInstance(); }
		catch (Exception e)
		    { 
			System.err.println("com.mchange.v2.log.MLog '" + classnames[i] + "' could not be loaded!"); 
			e.printStackTrace();
		    }
	    }
	return null;
    }

    public static MLog instance()
    { return mlog; }

    public static MLogger getLogger(String name) 
    {
	MLogger out;
	if ( transformer == null )
	    out = instance().getMLogger( name );
	else
	    {
		String xname = transformer.transformName( name );
		if (xname != null)
		    out = instance().getMLogger( xname );
		else
		    out = instance().getMLogger( name );
	    }
	return out;
    }

    public static MLogger getLogger(Class cl)
    {
	MLogger out;
	if ( transformer == null )
	    out = instance().getMLogger( cl );
	else
	    {
		String xname = transformer.transformName( cl );
		if (xname != null)
		    out = instance().getMLogger( xname );
		else
		    out = instance().getMLogger( cl );
	    }
	return out;
    }

    public static MLogger getLogger()
    {
	MLogger out;
	if ( transformer == null )
	    out = instance().getMLogger();
	else
	    {
		String xname = transformer.transformName();
		if (xname != null)
		    out = instance().getMLogger( xname );
		else
		    out = instance().getMLogger();
	    }
	return out;
    }

    public static void log(MLevel l, String msg)
    { instance().getLogger().log( l, msg ); }

    public static void log(MLevel l, String msg, Object param)
    { instance().getLogger().log( l, msg, param ); }

    public static void log(MLevel l,String msg, Object[] params)
    { instance().getLogger().log( l, msg, params ); }

    public static void log(MLevel l, String msg,Throwable t)
    { instance().getLogger().log( l, msg, t ); }

    public static void logp(MLevel l, String srcClass, String srcMeth, String msg)
    { instance().getLogger().logp( l, srcClass, srcMeth, msg ); }

    public static void logp(MLevel l, String srcClass, String srcMeth, String msg, Object param)
    { instance().getLogger().logp( l, srcClass, srcMeth, msg, param ); }

    public static void logp(MLevel l, String srcClass, String srcMeth, String msg, Object[] params)
    { instance().getLogger().logp( l, srcClass, srcMeth, msg, params ); }

    public static void logp(MLevel l, String srcClass, String srcMeth, String msg, Throwable t)
    { instance().getLogger().logp( l, srcClass, srcMeth, msg, t ); }

    public static void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg)
    { instance().getLogger().logp( l, srcClass, srcMeth, rb, msg ); }

    public static void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg, Object param)
    { instance().getLogger().logrb( l, srcClass, srcMeth, rb, msg, param ); }

    public static void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg, Object[] params)
    { instance().getLogger().logrb( l, srcClass, srcMeth, rb, msg, params ); }

    public static void logrb(MLevel l, String srcClass, String srcMeth, String rb, String msg, Throwable t)
    { instance().getLogger().logrb( l, srcClass, srcMeth, rb, msg, t ); }

    public static void entering(String srcClass, String srcMeth)
    { instance().getLogger().entering( srcClass, srcMeth ); }

    public static void entering(String srcClass, String srcMeth, Object param)
    { instance().getLogger().entering( srcClass, srcMeth, param ); }

    public static void entering(String srcClass, String srcMeth, Object params[])
    { instance().getLogger().entering( srcClass, srcMeth, params ); }

    public static void exiting(String srcClass, String srcMeth)
    { instance().getLogger().exiting( srcClass, srcMeth ); }

    public static void exiting(String srcClass, String srcMeth, Object result)
    { instance().getLogger().exiting( srcClass, srcMeth, result ); }

    public static void throwing(String srcClass, String srcMeth, Throwable t)
    { instance().getLogger().throwing( srcClass, srcMeth, t); }

    public static void severe(String msg)
    { instance().getLogger().severe( msg ); }

    public static void warning(String msg)
    { instance().getLogger().warning( msg ); }

    public static void info(String msg)
    { instance().getLogger().info( msg ); }

    public static void config(String msg)
    { instance().getLogger().config( msg ); }

    public static void fine(String msg)
    { instance().getLogger().fine( msg ); }

    public static void finer(String msg)
    { instance().getLogger().finer( msg ); }

    public static void finest(String msg)
    { instance().getLogger().finest( msg ); }

    public abstract MLogger getMLogger(String name);
    public abstract MLogger getMLogger(Class cl);
    public abstract MLogger getMLogger();
}