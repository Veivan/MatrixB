SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[spLoadFile]
	@FileName nvarchar(100)
	,@gender BIT
	,@ptype_id INT
AS
BEGIN
	SET NOCOUNT ON;
	
	DECLARE @cmd nvarchar(4000) = 'INSERT INTO mPicture(gender, ptype_id, fpicture) SELECT ' 
		+ CAST(@gender AS NVARCHAR) + ',' + CAST(@ptype_id AS NVARCHAR)
		+ ', * FROM OPENROWSET(BULK ''' 
		+ @FileName + ''', SINGLE_BLOB) AS Picture'
	--PRINT @cmd
	EXEC (@cmd)
END
GO
