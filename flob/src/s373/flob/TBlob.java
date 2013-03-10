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
 */
package s373.flob;


/**
* 
* trackedBlob is now TBlob.TBlob extends ABlob. 
* Has more internal variables like vel, id, birthtime,...
* Provides better id tracking & vel calculations.
* 
*/
public class TBlob extends ABlob{
	public boolean newblob=false;
	public long birthtime;
	public long lifetime=Flob.TBlobLifeTime;
	public int presencetime;
	public boolean linked=false;

	public float pcx,pcy;
	public float velx,vely;
	public float prevelx,prevely;
	public float maxdist2=Flob.TBlobMaxDistSquared;
	public float rad,rad2;



	// cast ABlob->TBlob new trackedblob
	TBlob(ABlob b){	
		
		// ABlob cp sans vel
		
//		id = ImageBlobs.idnumbers++; //b.id;
		id = ImageBlobs.idnumbers; //b.id;
		pixelcount = b.pixelcount;
		boxminx = b.boxminx;
		boxminy = b.boxminy;
		boxmaxx = b.boxmaxx;
		boxmaxy = b.boxmaxy;
		boxcenterx = b.boxcenterx;
		boxcentery = b.boxcentery;
		boxdimx = b.boxdimx;
		boxdimy = b.boxdimy;
		pboxcenterx = boxcenterx;
		pboxcentery = boxcentery;
//		ivelx = b.ivelx;
//		ively = b.ively;
		
		cx = b.cx;
		cy = b.cy;
		bx = b.bx;
		by = b.by;
		dimx = b.dimx;
		dimy = b.dimy;

		armleftx = b.armleftx;
		armlefty = b.armlefty;
		armrightx = b.armrightx;
		armrighty = b.armrighty;
		headx = b.headx;
		heady = b.heady;
		bottomx = b.bottomx;
		bottomy = b.bottomy;
		footleftx = b.footleftx;
		footlefty = b.footlefty;
		footrightx = b.footrightx;
		footrighty = b.footrighty;

		
		// TBlob specific
		
		newblob=true;
		birthtime=System.currentTimeMillis();
		lifetime = Flob.TBlobLifeTime;
		presencetime = 1;
		linked=false;

		pcx = cx;
		pcy = cy;
		prevelx=prevely=velx=vely=0.0f;
		maxdist2=Flob.TBlobMaxDistSquared;//2555f;//1000;//~31px//100;
		calcrad();
	
	}

	void calcrad(){
		rad = (boxdimx<boxdimy)?boxdimx/2f:boxdimy/2f;
		rad2 = rad*rad;
	}
}


//backwards compatability not so good with java
//typedef trackedBlob TBlob;
//
//package s373.flob;
//
//public class trackedBlob extends TBlob {
//
//	trackedBlob(ABlob ab){
//		super(ab);
//	}
//}
