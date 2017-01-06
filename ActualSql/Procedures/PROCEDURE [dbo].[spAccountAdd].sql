SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Add or update account
-- =============================================
ALTER PROCEDURE [dbo].[spAccountAdd]
	@user_id BIGINT OUTPUT,
	@name NVARCHAR(50),  
	@screen_name NVARCHAR(150),
	@email NVARCHAR(50),  
	@phone NVARCHAR(50),  
	@pass NVARCHAR(50),  
	@twitter_id BIGINT,
	@group_id INT
AS BEGIN
	SET NOCOUNT ON

	IF @screen_name IS NULL SET @screen_name = @name

	IF (@user_id IS NULL) BEGIN
		INSERT INTO [dbo].[mAccounts]
			   ([name]
			   ,[screen_name]
			   ,[email]
			   ,[phone]
			   ,[pass]
			   ,[twitter_id]
			   ,[finsert])
		 VALUES
			   (@name, @screen_name, @email, @phone, @pass, @twitter_id, GETDATE())
		SET @user_id = @@IDENTITY
	END
	ELSE 
		UPDATE [dbo].[mAccounts]
		   SET [name] = @name
			  ,[screen_name] = @screen_name
			  ,[email] = @email
			  ,[phone] = @phone
			  ,[pass] = @pass
			  ,[twitter_id] = @twitter_id
		 WHERE [user_id] = @user_id

	MERGE [dbo].[mBelong2] B
	USING (SELECT [group_id] = @group_id, [user_id] = @user_id ) I 
		ON B.[group_id] = I.[group_id] AND B.[user_id] = I.[user_id]
	WHEN NOT MATCHED BY TARGET THEN INSERT
		( [group_id], [user_id])
	VALUES
		(@group_id, @user_id)
	;	
	
END
GO