// 自定义JavaScript功能

// 点赞功能
function toggleLike(commentId) {
    const likeBtn = document.getElementById('like-btn-' + commentId);
    const isLiked = likeBtn.classList.contains('liked');
    
    const url = isLiked ? '/comment/unlike/' + commentId : '/comment/like/' + commentId;
    
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            likeBtn.classList.toggle('liked');
            document.getElementById('like-count-' + commentId).textContent = data.likeCount;
        } else {
            alert(data.message || '操作失败');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('操作失败，请稍后重试');
    });
}

// 收藏功能
function toggleFavorite(gameId) {
    const favoriteBtn = document.getElementById('favorite-btn-' + gameId);
    const isFavorited = favoriteBtn.classList.contains('favorited');
    
    const url = isFavorited ? '/favorite/remove' : '/favorite/add';
    
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: 'gameId=' + gameId
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            favoriteBtn.classList.toggle('favorited');
            const icon = favoriteBtn.querySelector('i');
            if (icon) {
                icon.classList.toggle('fas');
                icon.classList.toggle('far');
            }
            favoriteBtn.querySelector('.btn-text').textContent = 
                isFavorited ? '收藏' : '已收藏';
        } else {
            alert(data.message || '操作失败');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('操作失败，请稍后重试');
    });
}

// 图片预览功能
function previewImage(input, previewId) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        
        reader.onload = function(e) {
            const preview = document.getElementById(previewId);
            if (preview) {
                preview.src = e.target.result;
                preview.style.display = 'block';
            }
        };
        
        reader.readAsDataURL(input.files[0]);
    }
}

// 确认删除对话框
function confirmDelete(message) {
    return confirm(message || '确定要删除吗？此操作不可恢复。');
}

// 表单验证
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return false;
    
    const inputs = form.querySelectorAll('input[required], textarea[required]');
    for (let input of inputs) {
        if (!input.value.trim()) {
            alert('请填写所有必填项');
            input.focus();
            return false;
        }
    }
    return true;
}

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    // 自动隐藏提示消息
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });
    
    // 初始化工具提示
    const tooltips = document.querySelectorAll('[data-toggle="tooltip"]');
    tooltips.forEach(tooltip => {
        new bootstrap.Tooltip(tooltip);
    });
});
