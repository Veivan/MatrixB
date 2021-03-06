USE [MatrixB]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[mRandText](
	[rnt_id] [bigint] IDENTITY(1,1) NOT NULL,
	[randtext] [nvarchar](max) NOT NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

-- ================================================
-- Author:	Vetrov
-- Description:	Store random texts in DB
-- ================================================
CREATE PROCEDURE [dbo].[spRandTextAdd]
	@text NVARCHAR(MAX)
AS BEGIN
	SET NOCOUNT ON;

	MERGE [dbo].[mRandText] T
	USING (SELECT [randtext] = @text ) I 
		ON T.[randtext] = I.[randtext]
	WHEN NOT MATCHED BY TARGET THEN INSERT
		([randtext])
	VALUES
		(@text)
	;
END
GO

UPDATE [dbo].[mDBversion]
   SET [version] = '2017.9.9.1'
GO

