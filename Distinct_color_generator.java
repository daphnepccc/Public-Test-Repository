Generates a given number of distinct colors for use with GUI for .NET e.g. UltraCharts.
I was looking for a way to have colors that are different looking, here's the result after some research and testing.

CLASS Libraries.ColorGenerator   USE-WIDGET-POOL  :
  DEFINE PUBLIC PROPERTY UniqueColor AS CLASS System.Drawing.Color NO-UNDO EXTENT
  GET.
  SET. 
    
  CONSTRUCTOR PUBLIC ColorGenerator ( piNumUniqueColorsToGenerate AS INTEGER ):
    DEFINE VARIABLE deColorSpacing       AS DECIMAL NO-UNDO.
    DEFINE VARIABLE iColorComponent      AS INTEGER NO-UNDO INITIAL 1.
    DEFINE VARIABLE iNumHueSteps         AS INTEGER NO-UNDO.
    DEFINE VARIABLE iNumSaturationSteps  AS INTEGER NO-UNDO.
    DEFINE VARIABLE iNumBrightnessSteps  AS INTEGER NO-UNDO.
    DEFINE VARIABLE deExtHueSteps        AS DECIMAL EXTENT NO-UNDO.
    DEFINE VARIABLE deExtSaturationSteps AS DECIMAL EXTENT NO-UNDO.
    DEFINE VARIABLE deExtBrightnessSteps AS DECIMAL EXTENT NO-UNDO.
    DEFINE VARIABLE iHueStepIndex        AS INTEGER NO-UNDO.
    DEFINE VARIABLE iSaturationStepIndex AS INTEGER NO-UNDO.
    DEFINE VARIABLE iBrightnessStepIndex AS INTEGER NO-UNDO.
    DEFINE VARIABLE iColorIndex          AS INTEGER NO-UNDO.
    /* number of distinguishable colors (hue) on a given Saturation and Brightness slice */
    &SCOPED-DEFINE MaxNumHueSteps 15
    /* under these levels, colors are hard to distinguish (too dark or too white) */
    &SCOPED-DEFINE MinimumSaturation 0.4    &SCOPED-DEFINE MinimumBrightness 0.5
    &SCOPED-DEFINE OneColorHue 220 /* blue */
    
    SUPER ().

    ASSIGN
      iNumHueSteps                 = MINIMUM({&MaxNumHueSteps}, piNumUniqueColorsToGenerate)
      EXTENT(UniqueColor)          = piNumUniqueColorsToGenerate
      deColorSpacing               = SQRT((piNumUniqueColorsToGenerate / iNumHueSteps )) /* split the rest between Saturation and Brightness */
      iNumSaturationSteps          = System.Math:Ceiling(deColorSpacing)
      iNumBrightnessSteps          = iNumSaturationSteps
      EXTENT(deExtHueSteps)        = iNumHueSteps
      EXTENT(deExtBrightnessSteps) = iNumBrightnessSteps
      EXTENT(deExtSaturationSteps) = iNumSaturationSteps
    .

    IF iNumHueSteps > 1
    THEN DO iHueStepIndex = 1 TO iNumHueSteps:
      deExtHueSteps[iHueStepIndex] = (360 / iNumHueSteps) * (iHueStepIndex - 1).
    END. /* DO iHueStepIndex = 1 TO iNumHueSteps */
    ELSE deExtHueSteps[1] = {&OneColorHue}.

    IF iNumSaturationSteps > 1
    THEN DO iSaturationStepIndex = 1 TO iNumSaturationSteps:
      deExtSaturationSteps[iSaturationStepIndex] = 1 - (((1 - {&MinimumSaturation}) / (iNumSaturationSteps - 1)) * (iSaturationStepIndex - 1)). /* the first will be the minimum, the others will be spaced evenly in the remaining available space */
    END. /* THEN DO iSaturationStepIndex = 1 TO iNumSaturationSteps: */
    ELSE deExtSaturationSteps[1] = 1.
    
    IF iNumBrightnessSteps > 1
    THEN DO iBrightnessStepIndex = 1 TO iNumBrightnessSteps:
      deExtBrightnessSteps[iBrightnessStepIndex] = 1 - (((1 - {&MinimumBrightness}) / (iNumBrightnessSteps - 1)) * (iBrightnessStepIndex - 1)). /* the first will be the minimum, the others will be spaced evenly in the remaining available space */
    END. /* THEN DO iBrightnessStepIndex = 1 TO iNumBrightnessSteps: */
    ELSE deExtBrightnessSteps[1] = 1.

    ASSIGN
     iHueStepIndex        = 1
     iSaturationStepIndex = 1
     iBrightnessStepIndex = 1
    .

    DO iColorIndex = 1 TO piNumUniqueColorsToGenerate:
      UniqueColor[iColorIndex] = GetRGBColorFromHSB(deExtHueSteps[iHueStepIndex],
                                                    deExtSaturationSteps[iSaturationStepIndex],
                                                    deExtBrightnessSteps[iBrightnessStepIndex]).
      IF iHueStepIndex < iNumHueSteps
      THEN iHueStepIndex = iHueStepIndex + 1.
      ELSE IF iSaturationStepIndex < iNumSaturationSteps
      THEN ASSIGN
       iSaturationStepIndex = iSaturationStepIndex + 1
       iHueStepIndex        = 1
      .
      ELSE IF iBrightnessStepIndex < iNumBrightnessSteps
      THEN ASSIGN
       iBrightnessStepIndex = iBrightnessStepIndex + 1
       iSaturationStepIndex = 1
       iHueStepIndex        = 1
      .
    END. /* DO iColorIndex = 1 TO piNumUniqueColorsToGenerate */
  END CONSTRUCTOR.
  
  METHOD PUBLIC System.Drawing.Color GetRGBColorFromHSB(
   deHue        AS DECIMAL, /* 0 to 360 degrees on the HSB cone, the color type (such as red, blue, or yellow), each value corresponds to one color : 0 is red, 45 is a shade of orange and 55 is a shade of yellow */
   deSaturation AS DECIMAL, /* 0 to 1 saturation, the intensity of the color, 0 means no color, that is a shade of grey between black and white; 1 means intense color */
   deBrightness AS DECIMAL /* also called Value, the brightness of the color, 0 is always black; depending on the saturation, 1 may be white or a more or less saturated color */
   ):
    DEFINE VARIABLE deRed AS DECIMAL NO-UNDO INITIAL 0.
    DEFINE VARIABLE deGreen AS DECIMAL NO-UNDO INITIAL 0.
    DEFINE VARIABLE deBlue AS DECIMAL NO-UNDO INITIAL 0.
    DEFINE VARIABLE deSectorPosition AS DECIMAL NO-UNDO.
    DEFINE VARIABLE deFractionOfSector AS DECIMAL NO-UNDO.
    DEFINE VARIABLE iSectorNumber AS INTEGER NO-UNDO.
    DEFINE VARIABLE dePAxis AS DECIMAL NO-UNDO.
    DEFINE VARIABLE deQAxis AS DECIMAL NO-UNDO.
    DEFINE VARIABLE deTAxis AS DECIMAL NO-UNDO.
    
    
    IF deSaturation > 0
    THEN DO:
      ASSIGN
       deHue = MIN(deHue, 360)
       deSectorPosition   = deHue / 60.0
       iSectorNumber      = TRUNCATE(deSectorPosition, 0)
       deFractionOfSector = deSectorPosition - iSectorNumber
       dePAxis            = deBrightness * (1 - deSaturation)
       deQAxis            = deBrightness * (1 - (deSaturation * deFractionOfSector))
       deTAxis            = deBrightness * (1 - (deSaturation * (1 - deFractionOfSector)))
      .

      CASE iSectorNumber:
        WHEN 0
        THEN ASSIGN
         deRed   = deBrightness
         deGreen = deTAxis
         deBlue  = dePAxis
        .
        WHEN 1
        THEN ASSIGN
         deRed   = deQAxis
         deGreen = deBrightness
         deBlue  = dePAxis
        .
        WHEN 2
        THEN ASSIGN
         deRed   = dePAxis
         deGreen = deBrightness
         deBlue  = deTAxis
        .
        WHEN 3
        THEN ASSIGN
         deRed   = dePAxis
         deGreen = deQAxis
         deBlue  = deBrightness
        .
        WHEN 4
        THEN ASSIGN
         deRed   = deTAxis
         deGreen = dePAxis
         deBlue  = deBrightness
        .
        WHEN 5
        THEN ASSIGN
         deRed   = deBrightness
         deGreen = dePAxis
         deBlue  = deQAxis
        .
      END CASE. /* CASE iSectorNumber */
    END. /* IF deSaturation > 0 */
    
    RETURN System.Drawing.Color:FromArgb(INTEGER(deRed * 255), INTEGER(deGreen * 255), INTEGER(deBlue * 255)).
  END METHOD.
END CLASS.
