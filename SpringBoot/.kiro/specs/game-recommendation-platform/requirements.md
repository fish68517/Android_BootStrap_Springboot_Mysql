# Requirements Document

## Introduction

游戏推荐与管理平台是一个基于Spring Boot的Web应用系统，支持三种用户角色（普通用户、游戏发布者、管理员）。系统提供游戏发布、审核、推荐、评论、收藏等核心功能，通过收集用户偏好实现个性化游戏推荐。

## Glossary

- **System**: 游戏推荐与管理平台 (Game Recommendation Platform)
- **User**: 普通用户，可以浏览游戏、发表评论、收藏游戏
- **Publisher**: 游戏发布者，可以发布和管理游戏信息
- **Admin**: 管理员，负责审核游戏、评论和撤回申请
- **Game**: 游戏信息，包含标题、描述、封面图等
- **Comment**: 用户对游戏的评论
- **Preference**: 用户的游戏偏好（如RPG、策略、射击等）
- **Recommendation**: 管理员基于用户偏好的游戏推荐
- **Withdrawal Request**: 游戏发布者提交的游戏撤回申请

## Requirements

### Requirement 1: 用户注册与登录

**User Story:** 作为一个新用户，我想要使用手机号或邮箱注册账号，以便我可以访问系统功能

#### Acceptance Criteria

1. WHEN a User submits registration form with username and password, THE System SHALL validate the username format as email or phone number
2. WHEN a User submits valid registration information, THE System SHALL create a new user account with role set to 'user' by default
3. WHEN a User submits a username that already exists, THE System SHALL return an error message indicating duplicate username
4. WHEN a registered User submits valid login credentials, THE System SHALL authenticate the User and create a session
5. WHEN a User submits invalid login credentials, THE System SHALL return an authentication error message

### Requirement 2: 用户角色与权限管理

**User Story:** 作为系统管理员，我想要区分不同的用户角色，以便控制不同用户的访问权限

#### Acceptance Criteria

1. THE System SHALL support three user roles: 'user', 'publisher', and 'admin'
2. WHEN a User with role 'user' attempts to access publisher functions, THE System SHALL deny access
3. WHEN a User with role 'publisher' attempts to access admin functions, THE System SHALL deny access
4. WHEN a User with role 'admin' accesses any function, THE System SHALL grant full access
5. THE System SHALL assign role 'user' to newly registered accounts by default

### Requirement 3: 用户资料管理

**User Story:** 作为已登录用户，我想要管理我的个人资料，以便更新我的信息或注销账号

#### Acceptance Criteria

1. WHEN a logged-in User requests to view profile, THE System SHALL display user information including username, nickname, and role
2. WHEN a logged-in User submits updated profile information, THE System SHALL validate and save the changes
3. WHEN a logged-in User requests account deletion, THE System SHALL remove the user account and all associated data
4. WHEN an Admin views user list, THE System SHALL display all registered users with their information
5. WHEN an Admin modifies any user information, THE System SHALL save the changes and log the operation

### Requirement 4: 用户偏好收集

**User Story:** 作为普通用户，我想要设置我的游戏偏好，以便系统能够为我推荐合适的游戏

#### Acceptance Criteria

1. WHEN a User logs in for the first time, THE System SHALL prompt the User to select game preferences
2. THE System SHALL provide predefined preference options including RPG, Strategy, FPS, Simulation, and Casual
3. WHEN a User selects preferences, THE System SHALL save the user-preference associations
4. WHEN a User updates preferences, THE System SHALL replace existing preferences with new selections
5. WHEN an Admin views user preferences, THE System SHALL display all users and their selected preferences

### Requirement 5: 游戏信息发布

**User Story:** 作为游戏发布者，我想要发布游戏信息，以便让用户了解和体验我的游戏

#### Acceptance Criteria

1. WHEN a Publisher submits game information with title and description, THE System SHALL create a new game record with status 'pending'
2. WHEN a Publisher uploads a cover image, THE System SHALL store the image URL in the game record
3. WHEN a Publisher uploads additional images, THE System SHALL store multiple image URLs separated by commas
4. THE System SHALL record the publisher_id and submission timestamp for each game
5. WHEN a Publisher views their games, THE System SHALL display all games published by that Publisher

### Requirement 6: 游戏审核管理

**User Story:** 作为管理员，我想要审核待发布的游戏，以便确保游戏内容符合平台规范

#### Acceptance Criteria

1. WHEN an Admin views pending games, THE System SHALL display all games with status 'pending'
2. WHEN an Admin approves a game, THE System SHALL update game status to 'approved' and record admin_id and review timestamp
3. WHEN an Admin rejects a game, THE System SHALL update game status to 'rejected' and record admin_id and review timestamp
4. WHEN a game status is 'approved', THE System SHALL make the game visible to all users
5. WHEN a Publisher views a rejected game, THE System SHALL allow the Publisher to modify and resubmit the game

### Requirement 7: 游戏推荐功能

**User Story:** 作为管理员，我想要根据用户偏好推荐游戏，以便提升用户体验

#### Acceptance Criteria

1. WHEN an Admin creates a recommendation, THE System SHALL associate the recommendation with a specific user and game
2. WHEN an Admin creates a recommendation, THE System SHALL allow the Admin to provide a reason for the recommendation
3. WHEN a User views their homepage, THE System SHALL display all recommendations created for that User
4. THE System SHALL only recommend games with status 'approved'
5. WHEN a User views a recommendation, THE System SHALL display the game information and recommendation reason

### Requirement 8: 评论发布与管理

**User Story:** 作为普通用户，我想要对游戏发表评论，以便分享我的游戏体验

#### Acceptance Criteria

1. WHEN a User submits a comment on an approved game, THE System SHALL create a comment record with status 'pending'
2. THE System SHALL record the user_id, game_id, content, and timestamp for each comment
3. WHEN an Admin approves a comment, THE System SHALL update comment status to 'approved' and make it visible
4. WHEN an Admin rejects a comment, THE System SHALL update comment status to 'rejected'
5. WHEN a User or Admin deletes their own comment, THE System SHALL remove the comment record

### Requirement 9: 评论点赞功能

**User Story:** 作为普通用户，我想要给认同的评论点赞，以便表达我的支持

#### Acceptance Criteria

1. WHEN a User clicks like on an approved comment, THE System SHALL create a like record with user_id and comment_id
2. WHEN a User clicks like on an already-liked comment, THE System SHALL remove the like record
3. WHEN a User views comments, THE System SHALL display the total like count for each comment
4. WHEN a User views comments, THE System SHALL indicate which comments the User has liked
5. THE System SHALL prevent duplicate like records for the same user and comment combination

### Requirement 10: 游戏收藏功能

**User Story:** 作为普通用户，我想要收藏感兴趣的游戏，以便快速访问我喜欢的游戏

#### Acceptance Criteria

1. WHEN a User clicks favorite on an approved game, THE System SHALL create a favorite record with user_id and game_id
2. WHEN a User clicks favorite on an already-favorited game, THE System SHALL remove the favorite record
3. WHEN a User views their favorites, THE System SHALL display all games favorited by that User
4. THE System SHALL record the timestamp when a game is favorited
5. THE System SHALL prevent duplicate favorite records for the same user and game combination

### Requirement 11: 游戏撤回申请

**User Story:** 作为游戏发布者，我想要申请撤回已发布的游戏，以便处理版权或其他问题

#### Acceptance Criteria

1. WHEN a Publisher submits a withdrawal request for their game, THE System SHALL create a withdrawal request record with status 'pending'
2. WHEN a Publisher submits a withdrawal request, THE System SHALL require the Publisher to provide a reason
3. WHEN an Admin approves a withdrawal request, THE System SHALL delete the game and all associated data
4. WHEN an Admin rejects a withdrawal request, THE System SHALL update request status to 'rejected' and keep the game
5. WHEN an Admin views withdrawal requests, THE System SHALL display all pending requests with game and publisher information

### Requirement 12: 游戏搜索与浏览

**User Story:** 作为任何用户，我想要搜索和浏览游戏，以便找到感兴趣的游戏

#### Acceptance Criteria

1. WHEN a User submits a search query, THE System SHALL return games with titles matching the query
2. THE System SHALL only display games with status 'approved' to non-admin users
3. WHEN a User views game list, THE System SHALL display game title, cover image, and description
4. WHEN a User clicks on a game, THE System SHALL display detailed game information including all images
5. WHEN a User views game details, THE System SHALL display approved comments for that game
