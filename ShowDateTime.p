/*The following sample procedure, ShowDateTime.p, is a simple ABL application that accesses .NET objects. It makes use of some of the basic 
ABL features that support access to .NET objects, including an OpenEdge .NET class and two from among a set in-the-box controls that OpenEdge 
installs to support the GUI for .NET.  */

USING Infragistics.Win.Misc.* FROM ASSEMBLY.                        /* 1 */
USING System.Windows.Forms.* FROM ASSEMBLY.

/* Define object reference and data variables */                    /* 2 */
DEFINE VARIABLE rDateForm AS CLASS Progress.Windows.Form NO-UNDO.
DEFINE VARIABLE rOKButton AS CLASS UltraButton NO-UNDO.
DEFINE VARIABLE rDateField AS CLASS UltraLabel NO-UNDO.

/* Create objects */                                                /* 3 */
rDateForm = NEW Progress.Windows.Form( ).
rOKButton = NEW UltraButton( ).
rDateField = NEW UltraLabel( ).

/* Initialize OK button */                                          /* 4 */
rOKButton:Text = "OK".
rOKButton:Size = TextRenderer:MeasureText( "OK", 
                                           rOKButton:Font ).
rOKButton:Height = rOKButton:Height + 12.
rOKButton:Width = rOKButton:Width + 12.
rOKButton:DialogResult = DialogResult:OK.
rOKButton:Top = rDateField:Top + rDateField:Height + 4.

/* Initialize current date/time field */                            /* 5 */
rDateField:Text = STRING(System.DateTime:Now).
rDateField:Size = TextRenderer:MeasureText( rDateField:Text, 
                                            rDateField:Font ).
rDateField:Top = 2.

/* Initialize dialog with field and button */                       /* 6 */
rDateForm:Text = "Today's Date and Time".
rDateForm:MaximizeBox = FALSE.
rDateForm:MinimizeBox = FALSE.
rDateForm:FormBorderStyle = FormBorderStyle:FixedDialog.
rDateForm:Controls:Add( rDateField ).
rDateForm:Controls:Add( rOKButton ).
rDateForm:AcceptButton = rOKButton.

/* Adjust dialog size and controls for field and button */          /* 7 */
rDateForm:Width = rDateField:Width * 1.5.
rDateForm:Height = rOKButton:Top + rOKButton:Height + 
                   ( 2 * rDateField:Height ) + 24.
rDateField:Left = ( rDateForm:Width - rDateField:Width ) / 2.
rOKButton:Left = ( rDateForm:Width - rOKButton:Width ) / 2.

/* Show dialog and wait for button click */                         /* 8 */
WAIT-FOR rDateForm:ShowDialog( ).

rDateForm:Dispose( ).                                               /* 9 */
