SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[spAccountAdd]
	@user_id BIGINT OUTPUT,
	@name NVARCHAR(50),  
	@screen_name NVARCHAR(150),
	@email NVARCHAR(50),  
	@phone NVARCHAR(50),  
	@pass NVARCHAR(50),  
	@twitter_id BIGINT
AS BEGIN
	SET NOCOUNT ON

	IF (@user_id IS NULL)
		INSERT INTO [dbo].[mAccounts]
			   ([user_id]
			   ,[name]
			   ,[screen_name]
			   ,[email]
			   ,[phone]
			   ,[pass]
			   ,[twitter_id]
			   ,[finsert])
		 VALUES
			   (@user_id, @name, @screen_name, @email, @phone, @pass, @twitter_id, GETDATE())
	ELSE
		UPDATE [dbo].[mAccounts]
		   SET [name] = @name
			  ,[screen_name] = @screen_name
			  ,[email] = @email
			  ,[phone] = @phone
			  ,[pass] = @pass
			  ,[twitter_id] = @twitter_id
		 WHERE [user_id] = @user_id
	
END
GO
