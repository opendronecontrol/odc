
package com.codeminders.ardrone.data.decoder.ardrone10.video;

// Copyright � 2007-2011, PARROT SA, all rights reserved.

// DISCLAIMER
// The APIs is provided by PARROT and contributors "AS IS" and any express or
// implied warranties, including, but not limited to, the implied warranties of
// merchantability
// and fitness for a particular purpose are disclaimed. In no event shall PARROT
// and contributors be liable for any direct, indirect, incidental, special,
// exemplary, or
// consequential damages (including, but not limited to, procurement of
// substitute goods or services; loss of use, data, or profits; or business
// interruption) however
// caused and on any theory of liability, whether in contract, strict liability,
// or tort (including negligence or otherwise) arising in any way out of the use
// of this
// software, even if advised of the possibility of such damage.

// Author : Daniel Schmidt
// Publishing date : 2010-01-06
// based on work by : Wilke Jansoone

// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// - Redistributions of source code must retain the above copyright notice, this
// list of conditions, the disclaimer and the original author of the source
// code.
// - Neither the name of the PixVillage Team, nor the names of its contributors
// may be used to endorse or promote products derived from this software without
// specific prior written permission.

class ImageSlice
{
    MacroBlock[] MacroBlocks;

    ImageSlice(int macroBlockCount)
    {
        MacroBlocks = new MacroBlock[macroBlockCount];

        for(int index = 0; index < macroBlockCount; index++)
            MacroBlocks[index] = new MacroBlock();
    }
}