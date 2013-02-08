/**
 * Flob
 * Fast multi-blob detector and simple skeleton tracker using flood-fill algorithms.
 * http://s373.net/code/flob
 *
 * Copyright (C) 2008-2013 Andre Sier http://s373.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      Andre Sier 
 * @modified    20130208
 * @version     0.2.2y (22)
 * @url			http://s373.net/code/flob
 */

/**
 * version 0.2.2y documentation assistance and
 * by Mahesh Viswanathan <mahesh@tinymogul.com>
 * 
 * dont know how you want to be credited, please change this as you see fit
 */
package s373.flob;


//import s373.flob.baseBlob;
//import s373.flob.ABlob;
//import s373.flob.trackedBlob;
//import s373.flob.quadBlob;
//import s373.flob.ImageBlobs;
//import s373.flob.pt2;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;


/**           
 * Flob           <br/>
 * Fast multi-blob detector and simple skeleton tracker using flood-fill algorithms.           <br/>
 * http://s373.net/code/flob           <br/>
 *	 * Flob is a continuous frame differencing algorithm using flood fill procedures to calculate blobs.           <br/>
	 * basic process is comparing incoming image to a background image.           <br/>
	 * there are two main operating modes @om (flob.setOm()):           <br/>
	 * 	-STATIC_DIFFERENCE (0)           <br/>
	 * 		incoming image is compared to background. background is unchanged.           <br/>
	 * 	-CONTINUOUS_DIFFERENCE (1)           <br/>
	 * 		incoming image is compared to background. background is set to previous frame.           <br/>
	 * 	-CONTINUOUS_EASE_DIFFERENCE (2)           <br/>
	 * 		incoming image is compared to background. previous frame eased onto background pixels.           <br/>
	 *            <br/>
	 * Flob receives an ARGB Processing PImage as input and converts            <br/>
	 * rgb->luma (luminance, greyscale image) using one of several            <br/>
	 * methods specified through @colormode.           <br/>
	 * first the image is mirrored if specified through @mirrorX @mirrorY           <br/>
	 * then the image is converted from argb->luma using @colormode           <br/>
	 * possible @colormode values:           <br/>
	 * @RED           
	 * @GREEN         
	 * @BLUE          
	 * @LUMA601       
	 * @LUMA609       
	 * @LUMAUSER      
	 *            
	 * the greyscale luminance image is then binarized using the            <br/>
	 * @videothresh value as reference.           <br/>
	 *            <br/>
	 *            <br/>
           <br/>
	 * to calculate the binary image, now Flob takes @thresholdmode            <br/>
	 * to specify the operation to calculate the binary image.            <br/>
	 * possible values include:           <br/>
	 * 	- @ABS : absolute diference of incoming pixel versus background           <br/>
	 * 	- @LESSER : if incoming pixel less than threshold, mark as white pixel in binary image           <br/>
	 * 	- @GREATER : white if above @videothresh value           <br/>
           <br/>
           <br/>
 *           <br/>
 */           
public class Flob {

	public PApplet app;

	public ImageBlobs imageblobs;
	public PImage videoimg;
	public PImage videotex;
	public PImage videotexmotion;
	public PImage videotexbin;
	public PImage videoteximgmotion;
	public int backgroundPixels[];
	public int numPixels;
	public int videoresw = 128;
	public int videoresh = 128;
	public int presence = 0;
	public int videotexmode, pvideotexmode = 10000;

	public int videothresh = 50;
	public float videothreshf = 50;
	public int videofade = 50; // only in om > 0
	public float videofadef = 50; // only in continuous dif
	public boolean mirrorX, mirrorY;
	public float worldwidth, worldheight;
//	public boolean coordsmode = true;
	// no need, user may choose output dimensions 0-1 and go from there, changed worldwidth to float
	public int blur = 0;
	public boolean trackfeatures[];

	public boolean floatmode=false;
	public float floatsmooth=0.555f;
	public float backgroundLuma[];
	public float currentLuma[];
		
	public int om = STATIC_DIFFERENCE;										
	public static final int STATIC_DIFFERENCE = 0;
	public static final int CONTINUOUS_DIFFERENCE = 1;
	public static final int CONTINUOUS_EASE_DIFFERENCE = 2;
	public float continuous_ease = 0.05f;
	
	public int colormode = GREEN;
	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int BLUE = 2;
	public static final int LUMA601 = 3;
	public static final int LUMA709 = 4;
	public static final int LUMAUSER = 5;
	public static String redstr = "RED";
	public static String greenstr = "GREEN";
	public static String bluestr = "BLUE";
	public static String luma601str = "LUMA601";
	public static String luma709str = "LUMA709";
	public static String lumausrstr = "LUMAUSER";
	private String colorModeStr = "";
	public float lumausercoefs[] = { 0.10f, 0.75f, 0.25f };
	
	public int thresholdmode = ABS;
	public static final int ABS = 0;
	public static final int LESSER = 1;
	public static final int GREATER = 2;
		
	public int blobpixmin = 0;
	public int blobpixmax = 0;

	public static int trackedBlobLifeTime = 5; // 60
	public static float trackedBlobMaxDistSquared = 2555f;

	public static String VERSION = "flob 0.2.2y - built ";


	/**
	 * 
	 * Flob constructor usage summary:
	 * 
	 * public Flob(PApplet applet):
	 * 		output dimensions of blob coords (PApplet.width, PApplet.height) & 
	 * 		assumes incoming image 128px
	 * 
	 * 
	 * public Flob(PApplet applet, PImage video):
	 * 		output dimensions of blob coords (PApplet.width, PApplet.height) & 
	 * 		assumes incoming image dimensions of @video
	 * 
	 * public Flob(PApplet applet, PImage video, float w, float h):
	 * 		output dimensions of blob coords (@w, @h) & 
	 * 		assumes incoming image dimensions of @video
	 * 
	 * 
	 * public Flob(PApplet applet, int srcW, int srcH, float dstW, float dstH):
	 * 		output dimensions of blob coords (@dstW, @dstH) & 
	 * 		assumes incoming image dimensions of @srcW, @srcH
	 * 
	 * 
	 */
	public Flob(PApplet applet) {
		app = applet;
		videoresw = videoresh = 128;
		worldwidth = (float)applet.width;
		worldheight = (float)applet.height;
		setup();
	}

	public Flob(PApplet applet, PImage video) {
		app = applet;
		videoresw = video.width;
		videoresh = video.height;
		worldwidth = (float)applet.width;
		worldheight = (float)applet.height;
		setup();
	}

	public Flob(PApplet applet, PImage video, float w, float h) {
		app = applet;
		videoresw = video.width;
		videoresh = video.height;
		worldwidth = w;
		worldheight = h;
		setup();
	}

	public Flob(PApplet applet, int srcW, int srcH, float dstW, float dstH) {
		app = applet;
		videoresw = srcW;
		videoresh = srcH;
		worldwidth = dstW;
		worldheight = dstH;
		setup();
	}

	private void setup() {
		trackfeatures = new boolean[5];
		for (int i = 0; i < 5; i++) {
			trackfeatures[i] = false;
		}
		videoimg = app.createImage(videoresw, videoresh, PConstants.ARGB);
		videotexbin = app.createImage(videoresw, videoresh, PConstants.ARGB);
		videotexmotion = app.createImage(videoresw, videoresh, PConstants.ARGB);
		videoteximgmotion = app.createImage(videoresw, videoresh,
				PConstants.ARGB);
		videotex = app.createImage(videoresw, videoresh, PConstants.ARGB);
		numPixels = videoresw * videoresh;
		backgroundPixels = new int[numPixels];

		backgroundLuma = new float[numPixels];
		currentLuma = new float[numPixels];

		imageblobs = new ImageBlobs(this);// pass flob pointer

		version();
	}

	public PImage binarize(int pix[]) {
		// PImage img = new PImage(videoresw, videoresh);
		PImage img = app.createImage(videoresw, videoresh, PConstants.ARGB);
		img.loadPixels();
		for(int i=0; i<pix.length;i++){
			img.pixels[i] = pix[i];
		}
		img.updatePixels();
		return binarize(img);
	}

	/**
	 * Flob.binarize()  main image preprocessing stage. outputs prepocessed PImage for tracking algorithms.
	 * incorporates fast blur code by Mario Klingemann <http://incubator.quasimondo.com>
	 * 
	 * thanks Eduardo Pinto & Fausto Fonseca for feedback 
	 * during osomdopensamento.wordpress.com @ fbaul, 2009
	 * 
	 * 
	 * Flob receives an ARGB Processing PImage as input and converts 
	 * rgb->luma (luminance, greyscale image) using one of several 
	 * methods specified through @colormode.
	 * 
	 * binarize steps include:
	 * 
	 * receives argb PImages @video; 
	 * fastblurs' the Image using Mario Klingemann fast blur 1.1 code according to @blur parameter;
	 * mirrors if @mirrorX / @mirrorY activated;
	 * converts argb->luma derived from @colormode;
	 * compares luma image to @videothresh value according do @thresholdmode; 
	 * returns binary output image;
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * old comment left
	 * 
	 * first pass of the flob engine.<br>
	 * revised in version 001l to allow different color channel tracking.<br>
	 * transforms the input image in a black and white only image (binary
	 * image).<br>
	 * optionally insert a fastblur in the image. (if setBlur > 0, blur has that
	 * radius)<br>
	 * // nice fastblur insertion, thanks to fausto fonseca for showing the
	 * code, and to eduardo pinto for teasing me about it. it's fast and great! <br>
	 * // fast blur code by Mario Klingemann <http://incubator.quasimondo.com><br>
	 * returns a binary image suitable for the calc engine.<br>
	 * <br>
	 * built in fastblur filter if blurRadius > 0. <br>
	 * 
	 * @param PImage
	 * @return PImage
	 */
	public PImage binarize(PImage video) {

		// internal src image copy
		videoimg = video;

		// Mario Klingemann's fast blur since 2009
		if (blur > 0)
			videoimg = fastblur(videoimg, blur);

		videoimg.loadPixels();

		// pixelMirror implementation as
		if (mirrorX && mirrorY) {
			int[] image = new int[numPixels]; // one image to flipx&y
			for (int i = 0; i < numPixels; i++) {
				image[i] = videoimg.pixels[i];
			}

			for (int j = 0; j < videoresh; j++) {
				for (int i = 0; i < videoresw; i++) {
					videoimg.pixels[j * videoresw + i] = image[(videoresw - i - 1)
							+ (videoresh - j - 1) * videoresw];
				}
			}
			videoimg.updatePixels();

		} else if (mirrorX && !mirrorY) {
			int[] scanline = new int[videoresw]; // one hscanline
			for (int j = 0; j < videoresh; j++) {
				for (int i = 0; i < videoresw; i++) {
					scanline[(videoresw - i - 1)] = videoimg.pixels[j
							* videoresw + i];
					videoimg.pixels[j * videoresw + i] = scanline[i];
				}
			}
			videoimg.updatePixels();

		} else if (!mirrorX && mirrorY) {
			// working ok since 001j
			int[] scanline = new int[videoresh]; // one vscanline

			for (int i = 0; i < videoresw; i++) {
				for (int j = 0; j < videoresh; j++) {
					scanline[(videoresh - j - 1)] = videoimg.pixels[j
							* videoresw + i];
					videoimg.pixels[j * videoresw + i] = scanline[j];
				}
			}

			videoimg.updatePixels();
		}

		
		
		
		// begin processing input image to binary image 
		
		presence = 0;
//		videoimg.loadPixels();

		int currentVal = 0;
		int backgroundVal = 0;
		int diffVal = 0;

		float currentValf = 0, backgroundValf = 0, diffValf = 0;
		
		boolean fm = floatmode;
		float	fs = floatsmooth;
		int		cm = colormode;
		int		tm = thresholdmode;
		int		o = om;
		
		for (int i = 0; i < numPixels; i++) {
			
			int currColor = videoimg.pixels[i];
			int bkgdColor = backgroundPixels[i];
			
			
			switch (cm) {
					case RED:
						currentVal = (currColor) & 0xFF;
						backgroundVal = (bkgdColor) & 0xFF;
						break;
					default:	
					case GREEN:
						currentVal = (currColor >> 8) & 0xFF;
						backgroundVal = (bkgdColor >> 8) & 0xFF;
						break;
					case BLUE:
						currentVal = (currColor >> 16) & 0xFF;
						backgroundVal = (bkgdColor >> 16) & 0xFF;
						break;
					case LUMA601:
						float pixval = (0.299f * ((currColor) & 0xFF)
								+ 0.587f * ((currColor >> 8) & 0xFF) + 0.114f * ((currColor >> 16) & 0xFF)) + 0.5f; // CCIR
						float bgval =  (0.299f * ((bkgdColor) & 0xFF)
								+ 0.587f * ((bkgdColor >> 8) & 0xFF) + 0.114f * ((bkgdColor >> 16) & 0xFF)) + 0.5f; // CCIR
						currentVal = (int) pixval;
						backgroundVal = (int) bgval;
						break;
					case LUMA709:
						float pixval1 =  (0.2126f * ((currColor) & 0xFF)
								+ 0.7152f * ((currColor >> 8) & 0xFF) + 0.0722f * ((currColor >> 16) & 0xFF)) + 0.5f; // CCIR
						float bgval1 =  (0.2126f * ((bkgdColor) & 0xFF)
								+ 0.7152f * ((bkgdColor >> 8) & 0xFF) + 0.0722f * ((bkgdColor >> 16) & 0xFF)) + 0.5f; // CCIR
																													// 709
						currentVal = (int) pixval1;
						backgroundVal = (int) bgval1;
						break;
					case LUMAUSER:
						float pixval2 = (lumausercoefs[0] * ((currColor) & 0xFF)
								+ lumausercoefs[1] * ((currColor >> 8) & 0xFF) + lumausercoefs[2]
								* ((currColor >> 16) & 0xFF)) + 0.5f; // CCIR
																		// 709
						float bgval2 = (lumausercoefs[0] * ((bkgdColor) & 0xFF)
								+ lumausercoefs[1] * ((bkgdColor >> 8) & 0xFF) + lumausercoefs[2]
								* ((bkgdColor >> 16) & 0xFF)) + 0.5f; // CCIR
																		// 709
						currentVal = (int) pixval2;
						backgroundVal = (int) bgval2;
						break;
		
					}

			int binarize = 0;

			if(fm){
				if(Math.abs(currentLuma[i]-currentVal)>1e-5) currentLuma[i] += ((float)currentVal-currentLuma[i])*fs;
				if(Math.abs(backgroundLuma[i]-backgroundVal)>1e-5) backgroundLuma[i] += ((float)backgroundVal-backgroundLuma[i])*fs;
				currentValf = currentLuma[i];
				backgroundValf = backgroundLuma[i];
			
				switch(tm){
				case ABS: 
					diffValf = Math.abs(currentValf-backgroundValf); 
					if (diffValf >= videothreshf) {
						presence += 1;
						binarize = 255;
					}
					break;
				case LESSER: 
					diffValf = currentValf-backgroundValf; 
					if (diffValf <= videothreshf) {
						presence += 1;
						binarize = 255;
					}
					break;
				case GREATER: 
					diffValf = currentValf-backgroundValf; 
					if (diffValf >= videothreshf) {
						presence += 1;
						binarize = 255;
					}
					break;
				}
			} else {
			
				switch(tm){
				case ABS: 
					diffVal = Math.abs(currentVal-backgroundVal); 
					if (diffVal >= videothresh) {
						presence += 1;
						binarize = 255;
					}
					break;
				case LESSER: 
					diffVal = currentVal-backgroundVal; 
					if (diffVal <= videothresh) {
						presence += 1;
						binarize = 255;
					}
					break;
				case GREATER: 
					diffVal = currentVal-backgroundVal; 
					if (diffVal >= videothresh) {
						presence += 1;
						binarize = 255;
					}
					break;
				}
				
			}
			
			// encode pix to binary img
			videotexbin.pixels[i] = (binarize << 24) | (binarize << 16)	| (binarize << 8) | binarize;							
		}
		videotexbin.updatePixels();
		
		if(o>0){
			// now update motion img and use that as base for tracking
			videotexmotion.loadPixels();
			for (int i = 0; i < numPixels; i++) {
				if(fm){
					float valf = videotexmotion.pixels[i] & 0xff;//currentLuma[i]-backgroundLuma[i];
					valf -= videofadef;
					valf += videotexbin.pixels[i] & 0xff;
					valf = valf < 0.0f ? 0.0f : valf > 255.f ? 255.f : valf;
					int value = Math.round(valf);
					videotexmotion.pixels[i] = (value << 24) | (value << 16)
							| (value << 8) | value;
				} else {
					 int value = videotexmotion.pixels[i] & 0xff;
					 value -= videofade; // minus fade
					 value += videotexbin.pixels[i]  & 0xff; // + binary
					 value = value < 0 ? 0 : value > 255 ? 255 : value;
					 videotexmotion.pixels[i] = (value << 24) | (value << 16)
					 | (value << 8) | value;
				}
			}

			videotexmotion.updatePixels();

			if (o == 1)
				setBackground(videoimg);
			if (o == 2)
				easeBackground(videoimg);

			return videotexmotion;
		}
		
		return videotexbin;

	}

	/// io stuff

	/**
	 * returns the updated videotex (in case it needs updating)
	 * 
	 * @return PImage
	 */

	public PImage getSrcImage() {
		return updateVideoTex();
	}

	public PImage updateVideoTex() {
		if (videotexmode > 9)
			return videoimg;

		if (videotexmode == 3) {
			// much faster since 0022y
			videoteximgmotion.loadPixels();
			videoimg.loadPixels();
			videotexmotion.loadPixels();
			videotexbin.loadPixels();
			for (int i = 0; i < numPixels; i++) {
				int pixtexmotion = (om == 0) ? videotexbin.pixels[i]
						: videotexmotion.pixels[i];
				int piximg = videoimg.pixels[i];
				int pix = 0;
				if(pixtexmotion == 0){
					pix = piximg;
				} else {
					pix = pixtexmotion;//0xffffff					
				}

//				int pixr = (((pixtexmotion >> 16) & 0xff) + ((piximg >> 16) & 0xff));
//				pixr = (pixr > 255) ? 255 : pixr;
//				int pixg = (((pixtexmotion >> 8) & 0xff) + ((piximg >> 8) & 0xff));
//				pixg = (pixg > 255) ? 255 : pixg;
//				int pixb = (((pixtexmotion) & 0xff) + ((piximg) & 0xff));
//				pixb = (pixb > 255) ? 255 : pixb;

				videoteximgmotion.pixels[i] = pix;
				//(pixtexmotion << 24) | (pixr << 16) | (pixg << 8) | pixb;
			}
			videoteximgmotion.updatePixels();
		}

		if(videotexmode!=pvideotexmode){// && videotexchange ) {
			pvideotexmode = videotexmode;
			switch (videotexmode) {
			default:
			case 0:
				videotex = videoimg;
				break;
			case 1:
				videotex = videotexbin;
				break;
			case 2:
				videotex = videotexmotion;
				break;
			case 3:
				videotex = videoteximgmotion;
				break;
			}
		}

		videotex.updatePixels();
		return videotex;

	}

	/**
	 * set the videotex returned by flob.videotex <br>
	 * case 0: videotex = src videoimg as flob sees it (incoming image)<br>
	 * case 1: videotex = binary image result from om==0, incoming img vs static bg<br>
	 * case 2: videotex = binary image result from om>0, incoming img vs dynamic bg<br>
	 * case 3: videotex = image result from incoming img + binary image<br>
	 * 
	 * @return void
	 */
	public Flob setVideoTex(int t) {
		videotexmode = t;
//		videotexchange = true;
		return this;
	}

	/**
	 * setImage sets the videotex returned by flob.videotex or flob.getSrcImage
	 * case 0: videotex = src videoimg as flob sees it (incoming image)<br>
	 * case 1: videotex = binary image result from om==0, incoming img vs static bg<br>
	 * case 2: videotex = binary image result from om>0, incoming img vs dynamic bg<br>
	 * case 3: videotex = image result from incoming img + binary image<br>
	 * 
	 * @param t
	 * @return void
	 */
	public Flob setImage(int t) {
		setSrcImage(t);
		return this;
	}

	/**
	 * getImage gets the current video image worked inside flob
	 * 
	 * @return PImage
	 */
	public PImage getImage() {
		return getSrcImage();
	}

	/**
	 * setSrcImage sets the videotex returned by flob.videotex or
	 * flob.getSrcImage
	 * 
	 * @return void
	 */
	public Flob setSrcImage(int t) {
		videotexmode = t;
		return this;
	}

	/**
	 * setTrackFeatures turns on/off searching for feature points: armleft,
	 * armright, head, bottom for each blob
	 * if (tflob.trackfeatures[0]) b = calc_feature_head(b);
		if (tflob.trackfeatures[1]) b = calc_feature_arms(b);
		if (tflob.trackfeatures[2])b = calc_feature_feet(b);
		if (tflob.trackfeatures[3])b = calc_feature_bottom(b);
		
	 * @return void
	 */
	public Flob setTrackFeatures(boolean[] tf) {

		for (int i = 0; i < tf.length; i++)
			trackfeatures[i] = tf[i];

		return this;
	}

	/**
	 * getTrackFeatures gets the boolean array with on/off's for searching
	 * feature points: armleft, armright, head, bottom for each blob
	 * 
	 * @return boolean[]
	 */
	public boolean[] getTrackFeatures() {
		return trackfeatures;
	}

	/**
	 * getAnyFeatureActive true if any feature points on
	 * 
	 * @return boolean
	 */
	public boolean getAnyFeatureActive() {
		boolean active = (trackfeatures[0] || trackfeatures[1]
				|| trackfeatures[2] || trackfeatures[3] || trackfeatures[4]);
		return active;
	}

	/**
	 * set the om either CONTINUOUS_DIFFERENCE (1) or STATIC_DIFFERENCE (0) or CONTINUOUS_EASE_DIFFERENCE (2)
	 * 
	 * @return this
	 */
	public Flob setOm(int t) {
		if (t > 1)
			om = CONTINUOUS_EASE_DIFFERENCE;
		else if (t > 0)
			om = CONTINUOUS_DIFFERENCE;
		else
			om = STATIC_DIFFERENCE;

		return this;
	}

	/**
	 * get the current om, either CONTINUOUS_DIFFERENCE (1) or STATIC_DIFFERENCE (0)
	 * 
	 * @return int
	 */
	public int getOm() {
		return om;
	}

	/**
	 * are you using floatmode?
	 * @return @floatmode
	 */
	public boolean isFloatmode() {
		return floatmode;
	}

	/**
	 * activate floating point calculations in binarize image
	 * @param floatmode
	 */
	public void setFloatmode(boolean floatmode) {
		this.floatmode = floatmode;
	}

	/**
	 * @return the floatsmooth
	 */
	public float getFloatsmooth() {
		return floatsmooth;
	}

	/**
	 * @param floatsmooth the floatsmooth to set
	 */
	public void setFloatsmooth(float floatsmooth) {
		this.floatsmooth = floatsmooth;
	}

	/**
	 * get @thresholdmode
	 * 
	 * @return thresholdmode
	 */
	public int getThresholdmode() {
		return thresholdmode;
	}

	/**
	 * set @thresholdmode
	 * 	- @flob.ABS (0): absolute diference of incoming pixel versus background
	 * 	- @flob.LESSER (1): if incoming pixel less than threshold, mark as white pixel in binary image
	 * 	- @flob.GREATER (2): white if above @videothresh value
	 * 
	 * @param thresholdmode
	 * 
	 */
	public void setThresholdmode(int thresholdmode) {
		this.thresholdmode = thresholdmode;
	}

	/**
	 * set the max lifetime for a trackedblob
	 * 
	 * @return void
	 */
	public Flob settrackedBlobLifeTime(int t) {
		trackedBlobLifeTime = t;
		return this;
	}

	/**
	 * get the max lifetime for a trackedblob
	 * 
	 * @return int
	 */
	public int gettrackedBlobLifeTime() {
		return trackedBlobLifeTime;
	}

	/**
	 * @return the trackedBlobMaxDistSquared
	 */
	public static float getTrackedBlobMaxDistSquared() {
		return trackedBlobMaxDistSquared;
	}

	/**
	 * @param trackedBlobMaxDistSquared the trackedBlobMaxDistSquared to set
	 */
	public static void setTrackedBlobMaxDistSquared(float trackedBlobMaxDistSquared) {
		Flob.trackedBlobMaxDistSquared = trackedBlobMaxDistSquared;
	}

//	/**
//	 * set the coords mode for the blobs returns. if true, will scale to global
//	 * world coordinates, if false, each blob returns normalized coordinates
//	 * 
//	 * 
//	 * @return this
//	 */
//	public Flob setCoordsMode(boolean t) {
//		coordsmode = t;
//		return this;
//	}
//
//	/**
//	 * get the coords mode for the blobs returns. if true, will scale to global
//	 * world coordinates, if false, each blob returns normalized coordinates
//	 * 
//	 * @return boolean
//	 */
//	public boolean getCoordsMode() {
//		return coordsmode;
//	}

	/**
	 * set the colormode for the binarization stage. how to consider a diff pix
	 * from background on which channel. red, green, blue, luma
	 * 
	 * possible @colormode values:
	 * @flob.RED (0)
	 * @flob.GREEN (1)
	 * @flob.BLUE (2)
	 * @flob.LUMA601 (3)
	 * @flob.LUMA609 (4)
	 * @flob.LUMAUSER (5)
	 * 
	 * 
	 * @return this
	 */
	public Flob setColorMode(int t) {
		colormode = t < 0 ? 0 : t > 5 ? 5 : t;
		return this;
	}

	/**
	 * get selected colormode
	 * 
	 * @return String
	 */
	public String getColorMode() {
		switch (colormode) {
		case RED:
			colorModeStr = redstr;
			break;
		case GREEN:
			colorModeStr = greenstr;
			break;
		case BLUE:
			colorModeStr = bluestr;
			break;
		case LUMA601:
			colorModeStr = luma601str;
			break;
		case LUMA709:
			colorModeStr = luma709str;
			break;
		case LUMAUSER:
			colorModeStr = lumausrstr;
			break;
		}
		return colorModeStr;
	}

	/**
	 * set lumausercoefs
	 * 
	 * @return this
	 */
	public Flob setLumaUserCoefs(float data[]) {
		lumausercoefs[0] = data[0];
		lumausercoefs[1] = data[1];
		lumausercoefs[2] = data[2];
		return this;
	}

	/**
	 * get lumausercoefs
	 * 
	 * @return float[]
	 */
	public float[] getLumaUserCoefs() {

		return lumausercoefs;

	}

	/**
	 * sets the background to compare to to this PImage
	 * 
	 * @return this
	 */
	public Flob setBackground(PImage video) {
		video.loadPixels();
			System.arraycopy(video.pixels, 0, backgroundPixels, 0,
				video.pixels.length);
		return this;
	}

	/**
	 * ease the background to compare to to this PImage
	 * 
	 * @return this
	 */
	public Flob easeBackground(PImage video) {
		video.loadPixels();
			for (int i = 0; i < numPixels; i++) {
				backgroundPixels[i] += (int) ( ((float)video.pixels[i] - (float)backgroundPixels[i])* continuous_ease );
			}
		return this;
	}

	/**
	 * gets the background image
	 * 
	 * // todo: incorporate float here too, but not necessary, can access @backgroundPixelsF directly if needed
	 * 
	 * @return int[]
	 */
	public int[] getBackground() {

		return backgroundPixels;

	}

	/**
	 * set the threshold value to the image binarization. missing h for
	 * backwards compatibility
	 * 
	 * @return this
	 */
	public Flob setTresh(float t) {
		videothreshf = t;
		videothresh = (int) t;
		return this;
	}

	/**
	 * set the threshold value to the image binarization
	 * 
	 * @return this
	 */
	public Flob setThresh(float t) {
		videothresh = (int) t;
		videothreshf = t;
		return this;
	}

	/**
	 * get the threshold value to the image binarization
	 * 
	 * @return float @videothresh
	 */
	public float getThresh() {
		return videothreshf;
	}

	/**
	 * set the fade value in flob.om > 0
	 * 
	 * @return this
	 */
	public Flob setFade(float t) {
		videofadef = t;
		videofade = (int)t;
		return this;
	}

	/**
	 * get the fade value in flob.om > 0
	 * 
	 * @return float @videofade
	 */
	public float getFade() {
		return videofade;
	}

	/**
	 * mirror video data along X axis?
	 * 
	 * @return this
	 */
	public Flob mirrorX(boolean m) {
		mirrorX = m;
		return this;
	}

	/**
	 * mirror video data along Y axis?
	 * 
	 * @return this
	 */
	public Flob mirrorY(boolean m) {
		mirrorY = m;
		return this;
	}

	/**
	 * set mirror in XY axis with two booleans
	 * 
	 * @return this
	 */
	public Flob setMirror(boolean m0, boolean m1) {
		mirrorX = m0;
		mirrorY = m1;
		return this;
	}

	/**
	 * get mirror in XY axis
	 * 
	 * @return boolean[]
	 */
	public boolean[] getMirror() {
		boolean[] m = new boolean[2];
		m[0] = mirrorX;
		m[1] = mirrorY;
		return m;
	}

	/**
	 * set min numpixels to be considered a blob
	 * 
	 * @return this
	 */
	public Flob setMinNumPixels(int t) {
		imageblobs.setminpix(t);
		return this;
	}

	/**
	 * set max numpixels to be considered a blob
	 * 
	 * @return this
	 */
	public Flob setMaxNumPixels(int t) {
		imageblobs.setmaxpix(t);
		return this;
	}

	/**
	 * get min numpixels to be considered a blob
	 * 
	 * @return int
	 */
	public int getMinNumPixels() {
		return imageblobs.minpix;
	}

	/**
	 * get max numpixels to be considered a blob
	 * 
	 * @return int
	 */
	public int getMaxNumPixels() {
		return imageblobs.maxpix;
	}

	/**
	 * set tracking min dist to be the same blob
	 * 
	 * @return this
	 */
	public Flob setTrackingMinDist(float s) {
		imageblobs.trackingmindist = s; // thanks Niko Knappe for spotting this
		return this;
	}

	/**
	 * get tracking min dist to be the same blob
	 * 
	 * @return float
	 */
	public float getTrackingMinDist() {
		return imageblobs.trackingmindist;
	}

	/**
	 * set smooth factor for blob speeds changes
	 * 
	 * @return this
	 */
	public Flob setSmooth(float s) {
		imageblobs.setSmoothib(s);
		return this;
	}

	/**
	 * get smooth of blob speeds
	 * 
	 * @return float
	 */
	public float getSmooth() {
		return imageblobs.lp1;
	}

	/**
	 * set the blur amount on the image. 0 = off, > 5 high blur
	 * 
	 * @return void
	 */
	public Flob setBlur(int blur) {
		this.blur = blur;
		return this;
	}

	/**
	 * get the blur amount on the image. 0 = off, > 5 high blur
	 * 
	 * @return int
	 */
	public int getBlur() {
		return blur;
	}

	/**
	 * calcs with current PImage. PImage must be binary image by this stage.
	 * returns the arraylist of the blobs
	 * 
	 * @return ArrayList
	 */
	public ArrayList<ABlob> calc(PImage img) {
		imageblobs.calc(img);
		return imageblobs.theblobs;
	}

	/**
	 * calcs with current PImage. PImage must be binary image by this stage.
	 * returns the arraylist of trackedBlob elements
	 * 
	 * @return ArrayList
	 */
	public ArrayList<trackedBlob> track(PImage img) {
		imageblobs.calc(img); // calc current blobs
		imageblobs.dotracking();
		return imageblobs.trackedblobs;
	}

	/**
	 * tracksimple is good tracking code, maintains id's, speed's, presencetime
	 * for each trackedBlob returns the arraylist of trackedBlob elements
	 * 
	 * @return ArrayList
	 */

	public ArrayList<trackedBlob> tracksimple(PImage img) {
		imageblobs.calc(img);
		return imageblobs.tracksimpleAL();
	}

	/**
	 * calcsimple is naive tracking. works good in stable configs. returns the
	 * arraylist of trackedBlob elements
	 * 
	 * @return ArrayList
	 */

	public ArrayList<trackedBlob> calcsimple(PImage img) {
		imageblobs.calc(img);
		return imageblobs.calcsimpleAL();
	}

	/**
	 * calcs with current PImage. PImage must be binary image by this stage.
	 * returns the arraylist of the blobs
	 * 
	 * @return ArrayList
	 */
	public ArrayList<quadBlob> calcQuad(PImage img) {
		imageblobs.calcQuad(img);
		return imageblobs.quadblobslist;
	}

	/**
	 * getTrackedBlob returns the nth tracked blob of the tracker<br>
	 * returns the arraylist of trackedBlob elements<br>
	 * <br>
	 * a tracked blob holds:<br>
	 * <br>
	 * // pos & vel & dim results are local world coords<br>
	 * <br>
	 * // int tb.id;<br>
	 * // float tb.cx;<br>
	 * // float tb.cy;<br>
	 * // float tb.velx;<br>
	 * // float tb.vely;<br>
	 * // float tb.prevelx;<br>
	 * // float tb.prevely;<br>
	 * // int tb.presencetime;<br>
	 * // float tb.dimx;<br>
	 * // float tb.dimy;<br>
	 * // int tb.birthtime;<br>
	 * 
	 * 
	 * @return trackedBlob
	 */
	public trackedBlob getTrackedBlob(int i) {

		trackedBlob tb = imageblobs.trackedblobs.get(i);
		return tb;

	}

	/**
	 * getPreviousTrackedBlob returns the nth tracked previous blob of the
	 * tracker<br>
	 * returns one trackedBlob element<br>
	 * <br>
	 */

	public trackedBlob getPreviousTrackedBlob(int i) {

		trackedBlob tb = imageblobs.prevtrackedblobs.get(i);
		return tb;

	}

	public float[] getABlobExtreme(int i) {

		ABlob ab = imageblobs.theblobs.get(i);
		float coords[] = { ab.cx, ab.cy, 0 };

		// calc quadrant
		int wquad = 0; // 0 center, 1 topleft, 2 topright, 3 bottom right, 4
		// bottom left
		float bdx2 = ab.dimx / 2;
		float bdy2 = ab.dimy / 2;

		if (ab.cx < imageblobs.w2 && ab.cy < imageblobs.h2) {
			wquad = 1;
			coords[0] = ab.cx - bdx2;
			coords[1] = ab.cy - bdy2;
		} else if (ab.cx > imageblobs.w2 && ab.cy < imageblobs.h2) {
			wquad = 2;
			coords[0] = ab.cx + bdx2;
			coords[1] = ab.cy - bdy2;
		} else if (ab.cx > imageblobs.w2 && ab.cy > imageblobs.h2) {
			wquad = 3;
			coords[0] = ab.cx + bdx2;
			coords[1] = ab.cy + bdy2;
		} else if (ab.cx < imageblobs.w2 && ab.cy > imageblobs.h2) {
			wquad = 4;
			coords[0] = ab.cx - bdx2;
			coords[1] = ab.cy + bdy2;
		}

		coords[2] = wquad;
		// // ensure arms calc is on, feet also
		// if (ab.cx < imageblobs.w2) {
		// coords[0] = ab.armleftx;
		// } else if (ab.cx >= imageblobs.w2) {
		// coords[0] = ab.armrightx;
		// }
		// if (ab.cy < imageblobs.h2) {
		// coords[0] = ab.armleftx;
		// } else if (ab.cx >= imageblobs.w2) {
		// coords[0] = ab.armrightx;
		// }

		return coords;
		// return ab;
	}

	/**
	 * getABlob returns the nth calc'ed blob of the tracker<br>
	 * returns one ABlob element<br>
	 * <br>
	 * 
	 * @return ABlob
	 */
	public ABlob getABlob(int i) {
		return imageblobs.theblobs.get(i);
		// return ab;
	}

	public quadBlob getQuadBlob(int i) {
		return imageblobs.quadblobslist.get(i);
		// return qb;
	}

	/**
	 * getPreviousABlob returns the nth calc'ed previous blob of the tracker<br>
	 * returns one ABlob element<br>
	 * <br>
	 * 
	 * @return ABlob
	 */
	public ABlob getPreviousABlob(int i) {
		return imageblobs.prevblobs.get(i);
		// return ab;
	}

	// calcsimpleAL

	public float[] getTrackedSimpleBlob(int i) {
		float data[] = new float[12];
		trackedBlob tb = imageblobs.trackedblobs.get(i);
		data[0] = tb.id;
		data[1] = tb.cx * worldwidth;
		data[2] = tb.cy * worldheight;
		data[3] = tb.velx * worldwidth;
		data[4] = tb.vely * worldheight;
		data[5] = tb.prevelx * worldwidth;
		data[6] = tb.prevely * worldheight;
		data[7] = tb.presencetime;
		data[8] = tb.dimx * worldwidth;
		data[9] = tb.dimy * worldheight;
		data[10] = tb.rad * worldwidth;
		data[11] = tb.birthtime;

		return data;
	}

	/**
	 * getNumBlobs. should be called after calc.
	 * 
	 * @return int
	 */
	public int getNumBlobs() {
		return imageblobs.theblobs.size();
	}

	public int getNumTrackedBlobs() {
		return imageblobs.trackedblobs.size();
	}

	public int getNumTrackedSimpleBlobs() {
		return imageblobs.tbsimplelist.size();
	}

	public int getNumQuadBlobs() {
		return imageblobs.quadblobslist.size();
	}

	/**
	 * getTrackedBlobf returns the data of the nth tracked blob of the tracker
	 * as float[] returns the arraylist of trackedBlob elements
	 * 
	 * @return float[12]
	 */

	public float[] getTrackedBlobf(int i) {
		float data[] = new float[12];
		trackedBlob tb = imageblobs.trackedblobs.get(i);
		data[0] = tb.id;
		data[1] = tb.cx * worldwidth;
		data[2] = tb.cy * worldheight;
		data[3] = tb.velx * worldwidth;
		data[4] = tb.vely * worldheight;
		data[5] = tb.prevelx * worldwidth;
		data[6] = tb.prevely * worldheight;
		data[7] = tb.presencetime;
		data[8] = tb.dimx * worldwidth;
		data[9] = tb.dimy * worldheight;
		data[10] = tb.rad * worldwidth;
		data[11] = tb.birthtime;
		return data;
	}

	/**
	 * getPresence. returns the number of active pixels
	 * 
	 * @return int
	 */
	public int getPresence() {
		return presence;
	}

	/**
	 * getPresencef. returns the normalized number of active pixels
	 * 
	 * @return float
	 */
	public float getPresencef() {
		return ((float) presence / (float) numPixels);
	}

	/**
	 * getCentroids. returns all coordinates as normalized floats
	 * 
	 * @return float[]
	 */
	public float[] getCentroids() {
		int numblobs = imageblobs.theblobs.size();
		float centroids[] = new float[2 * numblobs];
		for (int i = 0; i < numblobs; i++) {
			ABlob blob = imageblobs.theblobs.get(i);
			centroids[i * 2 + 0] = blob.cx * worldwidth;
			centroids[i * 2 + 1] = blob.cy * worldheight;
		}
		return centroids;
	}

	public float[] getPreviousCentroids() {
		int numblobs = imageblobs.prevblobs.size();
		float centroids[] = new float[2 * numblobs];
		for (int i = 0; i < numblobs; i++) {
			ABlob blob = imageblobs.prevblobs.get(i);
			centroids[i * 2 + 0] = blob.cx * worldwidth;
			centroids[i * 2 + 1] = blob.cy * worldheight;
		}
		return centroids;
	}

	/**
	 * getCentroid int i. returns coordinates of this centroid as float[2]
	 * 
	 * @return float[]
	 */

	public float[] getCentroid(int i) {
		float centroid[] = new float[2];
		ABlob blob = imageblobs.theblobs.get(i);
		centroid[0] = blob.cx;// * (float)worldwidth; //already passed
		centroid[1] = blob.cy;// * (float)worldheight;
		return centroid;
	}

	/**
	 * getCentroidPixelcount int i. returns coordinates of this centroid +
	 * pixelcount as float[3]
	 * 
	 * @return float[]
	 */

	public float[] getCentroidPixelcount(int i) {
		float centroid[] = new float[3];
		ABlob blob = imageblobs.theblobs.get(i);
		centroid[0] = blob.cx;// * (float)worldwidth;
		centroid[1] = blob.cy;// * (float)worldheight;
		centroid[2] = blob.pixelcount;
		return centroid;
	}

	/**
	 * getPreviousCurrentCentroid int i. returns previous and current
	 * coordinates of this centroid as normalized float[4]
	 * 
	 * @return float[]
	 */

	public float[] getPreviousCurrentCentroid(int i) {
		float centroid[] = new float[4];
		ABlob blob = imageblobs.theblobs.get(i);
		centroid[0] = blob.cx;// * worldwidth;
		centroid[1] = blob.cy;// * worldheight;
		return centroid;
	}

	/**
	 * getPreviousCurrentCentroidMass int i. returns previous and current
	 * coordinates of this centroid as normalized float[4]
	 * 
	 * @return float[]
	 */

	public float[] getPreviousCurrentCentroidMass(int i) {
		float centroid[] = new float[5];
		ABlob blob = imageblobs.theblobs.get(i);
		centroid[0] = blob.cx;// * worldwidth;
		centroid[1] = blob.cy;// * worldheight;
		centroid[4] = blob.pixelcount;
		return centroid;
	}

	/**
	 * getDim int i. returns dimensions of the bounding box of this centroid as
	 * normalized float[2]
	 * 
	 * @return float[]
	 */

	public float[] getDim(int i) {
		float centroid[] = new float[2];
		ABlob blob = imageblobs.theblobs.get(i);
		centroid[0] = blob.dimx;// * worldwidth;
		centroid[1] = blob.dimy;// * worldheight;
		return centroid;
	}

	/**
	 * getBox int i. returns coordinates of this centroid's box as int[4] box
	 * min x + box min y + box max x + box max y
	 * 
	 * 
	 * 
	 * @return int[]
	 */

	public int[] getBox(int i) {

		int box[] = new int[4];

		ABlob blob = imageblobs.theblobs.get(i);
		box[0] = blob.boxminx;// boxcenterx;
		box[1] = blob.boxminy;// boxcentery;
		box[2] = blob.boxmaxx;// - blob.boxminx;
		box[3] = blob.boxmaxy;// - blob.boxminy;

		return box;

	}

	/**
	 * testPos int x, int y. tests a point coords x + y in the image map returns
	 * true if on a blob, false if not on a blob x and y should be constrained
	 * to src video dimensions
	 * 
	 * @return boolean
	 */

	public boolean testPos(int x, int y) {

		boolean px = false;
		try {
			px = imageblobs.imagemap[y * imageblobs.w + x];
		} catch (Exception e) {
			System.out.print("flob.testPos(x,y) access out of bound with : "
					+ x + " " + y + "\n" + e + "\n");
		}
		return px;

		// return imageblobs.imagemap[y*imageblobs.w + x];

	}

	/**
	 * testPos float x, float y. tests a point normalized coords x + y in the
	 * image map returns true if on a blob, false if not on a blob
	 * 
	 * @return boolean
	 */

	public boolean testPos(float x, float y) {
		x = app.constrain(x, 0.f, 1.f);
		y = app.constrain(y, 0.f, 1.f);
		int px = (int) (x * imageblobs.w);
		int py = (int) (y * imageblobs.h);
		return imageblobs.imagemap[py * imageblobs.w + px];

	}

	/**
	 * PImage img = fastblur(PImage img, int radius); Super Fast Blur v1.1 by
	 * Mario Klingemann http://incubator.quasimondo.com
	 * 
	 * @return PImage
	 */

	public PImage fastblur(PImage img, int radius) {
		// Super Fast Blur v1.1
		// by Mario Klingemann http://incubator.quasimondo.com
		//
		// Tip: Multiple invovations of this filter with a small
		// radius will approximate a gaussian blur quite well.

		if (radius < 1) {
			return img;
		}
		int w = img.width;
		int h = img.height;
		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;
		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, p1, p2, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];
		int vmax[] = new int[Math.max(w, h)];
		int[] pix = img.pixels;
		int dv[] = new int[256 * div];
		for (i = 0; i < 256 * div; i++) {
			dv[i] = (i / div);
		}

		yw = yi = 0;

		for (y = 0; y < h; y++) {
			rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				rsum += (p & 0xff0000) >> 16;
				gsum += (p & 0x00ff00) >> 8;
				bsum += p & 0x0000ff;
			}
			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
					vmax[x] = Math.max(x - radius, 0);
				}
				p1 = pix[yw + vmin[x]];
				p2 = pix[yw + vmax[x]];

				rsum += ((p1 & 0xff0000) - (p2 & 0xff0000)) >> 16;
				gsum += ((p1 & 0x00ff00) - (p2 & 0x00ff00)) >> 8;
				bsum += (p1 & 0x0000ff) - (p2 & 0x0000ff);
				yi++;
			}
			yw += w;
		}

		for (x = 0; x < w; x++) {
			rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;
				rsum += r[yi];
				gsum += g[yi];
				bsum += b[yi];
				yp += w;
			}
			yi = x;
			for (y = 0; y < h; y++) {
				pix[yi] = 0xff000000 | (dv[rsum] << 16) | (dv[gsum] << 8)
						| dv[bsum];
				if (x == 0) {
					vmin[y] = Math.min(y + radius + 1, hm) * w;
					vmax[y] = Math.max(y - radius, 0) * w;
				}
				p1 = x + vmin[y];
				p2 = x + vmax[y];

				rsum += r[p1] - r[p2];
				gsum += g[p1] - g[p2];
				bsum += b[p1] - b[p2];

				yi += w;
			}
		}

		img.updatePixels();

		return (img);

	}

	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public String version() {
		String post = "";
		try {
			// http://www.neowin.net/forum/index.php?showtopic=746508
			File jarFile = new File(this.getClass().getProtectionDomain()
					.getCodeSource().getLocation().toURI());
			post = "\n" + VERSION + new Date(jarFile.lastModified())
					+ " - http://s373.net/code/flob \n\n";

		} catch (URISyntaxException e1) {
			System.out.print("flob couldnt access file version. " + e1);
		}

		System.out.print(post);
		return post;
	}

}
