SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Fill workgroups randomly
-- =============================================
ALTER PROCEDURE [dbo].[spFillBelong2Rand]
AS BEGIN
	SET NOCOUNT ON
	DECLARE @top INT

	DELETE B
	FROM [dbo].[mBelong2] B
		INNER JOIN  [dbo].[DicGroups] G ON G.group_id = B.group_id
	WHERE G.forwork = 1
  
	DECLARE @accs TABLE(nnid INT IDENTITY(1,1), [user_id] INT )
	INSERT @accs ([user_id])
	SELECT A.[user_id] FROM dbo.mAccounts AS A WHERE A.[enabled] = 1
	ORDER BY NEWID()

	DECLARE @tbl TABLE(RowID INT IDENTITY(1, 1) PRIMARY KEY, [group_id] INT, [goal] INT) 
	DECLARE	@group_id INT, @goal INT, @count INT, @iRow INT  

	INSERT @tbl ([group_id], [goal])
	SELECT D.[group_id], [goal]
	FROM [dbo].[DicGroups] D
		INNER JOIN [dbo].[mGroupRegim] R ON R.[groupid] = D.[group_id]
	WHERE [forwork] = 1 AND ISNULL([goal], 0) > 0 
	ORDER BY [WakeHour]

	SET @count = @@ROWCOUNT 
	SET @iRow = 1 
	WHILE @iRow <= @count BEGIN 
		SELECT @group_id = [group_id], @goal = [goal] FROM @tbl WHERE RowID = @iRow 
		INSERT mBelong2 ([group_id], [user_id])
		SELECT TOP (@goal) @group_id, [user_id] FROM @accs  
		DELETE TOP (@goal) FROM @accs
		SET @iRow = @iRow + 1 
	END 
END
GO
