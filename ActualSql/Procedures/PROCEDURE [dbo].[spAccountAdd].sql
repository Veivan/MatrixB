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
	@screen_name NVARCHAR(150), --Login
	@email NVARCHAR(50),  
	@phone NVARCHAR(50),  
	@pass NVARCHAR(50),  
	@twitter_id BIGINT,
	@group_id INT,
	@mailpass NVARCHAR(50),
	@gender BIT
AS BEGIN
	SET NOCOUNT ON

	IF @name IS NULL SET @name = @screen_name
	IF @twitter_id = -1 SET @twitter_id = NULL

	IF (@user_id = -1) BEGIN
		SET @user_id = NULL
		SELECT @user_id = [user_id] FROM [dbo].[mAccounts]
		WHERE [name] = @name AND [email] = @email AND [pass] = @pass
	END

	IF (@user_id IS NULL) BEGIN
		INSERT INTO [dbo].[mAccounts]
			   ([name]
			   ,[screen_name]
			   ,[email]
			   ,[phone]
			   ,[pass]
			   ,[twitter_id]
			   ,[mailpass]
			   ,[gender]
			   ,[finsert])
		 VALUES
			   (@name, @screen_name, @email, @phone, @pass, @twitter_id, @mailpass, @gender, GETDATE())
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
			  ,[mailpass] = @mailpass
			  ,[gender] = @gender
		 WHERE [user_id] = @user_id

	IF (@group_id <> -1)
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
