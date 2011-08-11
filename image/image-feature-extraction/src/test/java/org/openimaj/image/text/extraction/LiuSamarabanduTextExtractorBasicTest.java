package org.openimaj.image.text.extraction;

/**
 * 
 */


import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openimaj.image.FImage;
import org.openimaj.image.Image;
import org.openimaj.image.ImageUtilities;

/**
 *	
 *
 *	@author David Dupplaw <dpd@ecs.soton.ac.uk>
 *  @created 28 Jul 2011
 *	@version $Author$, $Revision$, $Date$
 */
public class LiuSamarabanduTextExtractorBasicTest
{
	@Test
	public void testExtractRegions()
	{
		try
		{
			// Read the image
			FImage testImage = ImageUtilities.readF( 
					getClass().getResource("lab-sign.jpg") ).normalise();
			
			// Process the image
			LiuSamarabanduTextExtractorBasic te = 
				new LiuSamarabanduTextExtractorBasic();
			// te.setOCRProcessor( new Tess4JOCRProcessor() );
			te.processImage( testImage, (Image<?,?>)null );
			
			// Get the strings extracted from the image
			List<String> strings = te.getTextStrings();
			
			// The assertion here is dependent on the image used
			// Assert.assertTrue( strings.contains( "LABORATOIRE" ) );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
}
